package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.booths.BoothMemberResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class AddEditCoordinateMemberActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<AddPostValidationError> {
    private static final String TAG = "AddStudentActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etCountry)
    public EditText etCountry;

    @Bind(R.id.etPhone)
    public EditText etPhone;

    @Bind(R.id.btnAdd)
    public Button btnAdd;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.etdob)
    public EditText etdob;
    @Bind(R.id.etGender)
    public Spinner etGender;
    @Bind(R.id.etBlood)
    public Spinner etBlood;

    @Bind(R.id.etVoterId)
    public EditText etVoterId;
    @Bind(R.id.etSalary)
    public EditText etSalary;
    @Bind(R.id.etAadhar)
    public EditText etAadhar;

    private int currentCountry;

    LeafManager leafManager;

    private UploadImageFragment imageFragment;

    String group_id, team_id;

    BoothMemberResponse.BoothMemberData studentData;

    boolean submitted = false;
    private Menu menu;

    boolean isEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coordinate);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("className"));

        init();

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        etCountry.setText(str[0]);

        setImageFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);

        if(!isEdit){
            menu.findItem(R.id.menuDelete).setVisible(false);
        }
        if(studentData!=null){
            menu.findItem(R.id.menuAllowAddMember).setChecked(studentData.allowedToAddUser);
            menu.findItem(R.id.menuAllowPost).setChecked(studentData.allowedToAddTeamPost);
            menu.findItem(R.id.menuAllowCommnet).setChecked(studentData.allowedToAddTeamPostComment);
        }

        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }
            if (studentData == null)
                return true;

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete_cordinate), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showLoadingBar(progressBar,false);
                //    progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteUser(AddEditCoordinateMemberActivity.this, GroupDashboardActivityNew.groupId, studentData.id);
                }
            });
            return true;
        }

        if(item.getItemId() == R.id.menuAllowAddMember){
            AppLog.e(TAG,"menuAllowAddMember");
            menu.findItem(R.id.menuAllowAddMember).setChecked(!menu.findItem(R.id.menuAllowAddMember).isChecked());
            showLoadingBar(progressBar,false);
            //    progressBar.setVisibility(View.VISIBLE);
            leafManager.allowAddOtherMember(AddEditCoordinateMemberActivity.this, GroupDashboardActivityNew.groupId,team_id,studentData.id);
            return true;
        }
        if(item.getItemId() == R.id.menuAllowPost){
            AppLog.e(TAG,"menuAllowPost");
            menu.findItem(R.id.menuAllowPost).setChecked(!menu.findItem(R.id.menuAllowPost).isChecked());
            showLoadingBar(progressBar,false);
            //    progressBar.setVisibility(View.VISIBLE);
            leafManager.allowTeamPost(AddEditCoordinateMemberActivity.this, GroupDashboardActivityNew.groupId,team_id,studentData.id);
            return true;
        }
        if(item.getItemId() == R.id.menuAllowCommnet){
            AppLog.e(TAG,"menuAllowCommnet");
            menu.findItem(R.id.menuAllowCommnet).setChecked(!menu.findItem(R.id.menuAllowCommnet).isChecked());
            showLoadingBar(progressBar,false);
            //    progressBar.setVisibility(View.VISIBLE);
            leafManager.allowTeamPostComment(AddEditCoordinateMemberActivity.this, GroupDashboardActivityNew.groupId,team_id,studentData.id);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {

        leafManager = new LeafManager();

        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");

        isEdit = getIntent().getBooleanExtra("isEdit",false);


        String[] bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        String[] genderArray = getResources().getStringArray(R.array.gender_array);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
        etGender.setAdapter(genderAdapter);

        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        etBlood.setAdapter(bloodGrpAdapter);

        if(isEdit){
            studentData = new Gson().fromJson(getIntent().getStringExtra("student_data"), BoothMemberResponse.BoothMemberData.class);
            etName.setText(studentData.name);
            etPhone.setText(studentData.phone);
            etPhone.setEnabled(false);
            etdob.setText(studentData.dob);
            etVoterId.setText(studentData.voterId);
            etSalary.setText(studentData.salary);
            etAadhar.setText(studentData.aadharNumber);

            int index = 0;
            for (int i = 0; i < genderArray.length; i++) {
                if ((studentData.gender + "").equals(genderArray[i])) {
                    index = i;
                }
            }
            etGender.setSelection(index);


            index = 0;
            for (int i = 0; i < bloodGrpArray.length; i++) {
                if ((studentData.bloodGroup + "").equals(bloodGrpArray[i])) {
                    index = i;
                }
            }
            etBlood.setSelection(index);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setImageFragment() {

        imageFragment = UploadImageFragment.newInstance(isEdit?studentData.image:null, true, true);
        btnAdd.setText(isEdit?getResources().getString(R.string.lbl_update):getResources().getString(R.string.lbl_add));

        setTitle(isEdit? getResources().getString(R.string.title_cordinate_details)+" - (" + getIntent().getStringExtra("className") + ")":getResources().getString(R.string.title_add_cordinate));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private long lastClickTime = 0;

    @OnClick({R.id.btnAdd, R.id.etCountry, R.id.etdob})
    public void onClick(View view) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
        } else {
            return;
        }
        Log.e(TAG, "Tap : ");
        switch (view.getId()) {
            case R.id.btnAdd:

                if (isValid() && !submitted) {
                    submitted = true;

                    if(!isEdit){
                        studentData = new BoothMemberResponse.BoothMemberData();
                    }
                    studentData.name = etName.getText().toString();

                    if (imageFragment.isImageChanged && !TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                        studentData.image = imageFragment.getmProfileImage();
                    }
                    studentData.countryCode = "IN";
                    studentData.phone = etPhone.getText().toString();
                    studentData.dob = etdob.getText().toString();
                    studentData.gender = etdob.getText().toString();

                    if (etGender.getSelectedItemPosition() > 0) {
                        studentData.gender = etGender.getSelectedItem().toString();
                    } else {
                        studentData.gender = "";
                    }
                    if (etBlood.getSelectedItemPosition() > 0) {
                        studentData.bloodGroup = etBlood.getSelectedItem().toString();
                    } else {
                        studentData.bloodGroup = "";
                    }
                    studentData.voterId = etVoterId.getText().toString();
                    studentData.salary = etSalary.getText().toString();
                    studentData.aadharNumber = etAadhar.getText().toString();

                    AppLog.e(TAG, "send data : " + studentData);

                    showLoadingBar(progressBar,false);
                    //    progressBar.setVisibility(View.VISIBLE);
                    if(isEdit){
                        leafManager.updateBoothsMember(this, group_id, team_id, studentData.id, studentData);
                    }else {
                        leafManager.addCoordinateBooth(this, group_id, team_id, studentData);
                    }

                } else {
                    submitted = false;
                }
                break;
            case R.id.etCountry:
                SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
                break;
            case R.id.etdob:
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etdob.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
                break;

        }
    }

    private boolean isValid() {

        if (!isValueValid(etName)) {
            return false;
        } else if (currentCountry == 0) {
            Toast.makeText(this, getResources().getString(R.string.msg_selectcountry), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValueValid(etPhone)) {
            return false;
        }
        return true;
    }

    public class SMBDailogClickListener implements DialogInterface.OnClickListener {
        private int DIALOG_ID = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListView lw = ((AlertDialog) dialog).getListView();


            switch (DIALOG_ID) {

                case R.id.layout_country:
                    etCountry.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    currentCountry = lw.getCheckedItemPosition() + 1;
                    break;

            }
        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        submitted = false;
        if (progressBar != null)
            hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_UPDATE_BOOTH_MEMEBER:
                Toast.makeText(this, getResources().getString(R.string.toast_edit_coordinate), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case LeafManager.API_ADD_COORDINATE:
                Toast.makeText(this,  getResources().getString(R.string.toast_add_coordinate), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case LeafManager.API_DELETE_USER:
                Toast.makeText(this, getResources().getString(R.string.toast_delete_coordinate), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {

    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        submitted = false;
        if (progressBar != null)
            hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);

        if (msg.contains("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);

        submitted = false;
        if (progressBar != null)
            hideLoadingBar();
     //       progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
