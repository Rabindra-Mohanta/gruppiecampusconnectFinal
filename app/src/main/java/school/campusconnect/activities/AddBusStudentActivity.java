package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.bus.BusStudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class AddBusStudentActivity extends BaseActivity {
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

    @Bind(R.id.labelPhone)
    public TextView labelPhone;

    private int currentCountry;

    LeafManager leafManager;

    private UploadImageFragment imageFragment;

    String group_id, team_id;
    private boolean isEdit=false;

    BusStudentRes.StudentData studentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus_student);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_bus_student));

        init();

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        etCountry.setText(str[0]);

        setImageFragment();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(isEdit)
        {
            getMenuInflater().inflate(R.menu.menu_edit,menu);
        }

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

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.smb_delete_student), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteBusStudent(AddBusStudentActivity.this, GroupDashboardActivityNew.groupId, team_id,studentData.getUserId());
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {
        leafManager = new LeafManager();
        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");
        isEdit = getIntent().getBooleanExtra("isEdit",false);
        if(isEdit){
            studentData = new Gson().fromJson(getIntent().getStringExtra("student_data"), BusStudentRes.StudentData.class);
            etName.setText(studentData.name);
            etPhone.setText(studentData.phone);
            btnAdd.setVisibility(View.GONE);
        }

    }

    private void setImageFragment() {
        if (isEdit) {
            imageFragment = UploadImageFragment.newInstance(studentData.getImage(), true, true);
            btnAdd.setText("Update");
            setTitle(getResources().getString(R.string.title_student_details));
        } else {
            imageFragment = UploadImageFragment.newInstance(null, true, true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @OnClick({R.id.btnAdd, R.id.etCountry})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                if (isValid()) {
                    BusStudentRes.StudentData addStudentReq = new BusStudentRes.StudentData();
                    addStudentReq.name = etName.getText().toString();
                    String[] str = getResources().getStringArray(R.array.array_country_values);
                    addStudentReq.countryCode = str[currentCountry - 1];

                    if(isEdit){
                      /*  if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                            addStudentReq.image=null;
                        }
                        else
                        {
                            addStudentReq.image=imageFragment.getmProfileImage();
                        }
                        addStudentReq.phone = null;
                        AppLog.e(TAG, "send data : " + addStudentReq);
                        progressBar.setVisibility(View.VISIBLE);
                        leafManager.editClassStudent(this, group_id, team_id,studentData.getUserId(), addStudentReq);*/
                    }
                    else {
                        addStudentReq.phone = etPhone.getText().toString();
                        addStudentReq.image=imageFragment.getmProfileImage();
                        AppLog.e(TAG, "send data : " + addStudentReq);
                        progressBar.setVisibility(View.VISIBLE);
                        leafManager.addBusStudent(this, group_id, team_id, addStudentReq);
                    }
                }
                break;
            case R.id.etCountry:
                SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
                break;

        }
    }

    private boolean isValid() {

        if (!isValueValid(etName)) {
            return false;
        } else if (currentCountry == 0) {
            Toast.makeText(this, getResources().getString(R.string.msg_selectcountry), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValueValid(etPhone) && !isEdit) {
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
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_BUS_STUDENTS_ADD:
                Toast.makeText(this, "Add Student successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case LeafManager.API_EDIT_STUDENTS:
                Toast.makeText(this, "Edit Student successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case LeafManager.API_BUS_STUDENTS_DELETE:
                Toast.makeText(this, "Delete Student successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

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
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
