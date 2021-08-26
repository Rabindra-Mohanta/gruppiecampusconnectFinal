package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class AddStaffActivity extends BaseActivity {
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

    @Bind(R.id.etGender)
    public Spinner etGender;
    @Bind(R.id.etStudentId)
    public EditText etStudentId;
    @Bind(R.id.etDesig)
    public EditText etDesig;
    @Bind(R.id.etClass)
    public EditText etClass;
    @Bind(R.id.etQuali)
    public EditText etQuali;

    @Bind(R.id.etdob)
    public EditText etdob;
    @Bind(R.id.etdoj)
    public EditText etdoj;
    @Bind(R.id.etReligion)
    public EditText etReligion;
    @Bind(R.id.etCast)
    public EditText etCast;
    @Bind(R.id.etBlood)
    public Spinner etBlood;
    @Bind(R.id.etEmail)
    public EditText etEmail;
    @Bind(R.id.etAddress)
    public EditText etAddress;
    @Bind(R.id.labelPhone)
    public TextView labelPhone;


    private int currentCountry;

    LeafManager leafManager;

    private UploadImageFragment imageFragment;

    String group_id;
    private boolean isEdit = false;
    private boolean isAdmin = false;
    private boolean isPost = false;

    StaffResponse.StaffData studentData;
    private Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle("Add Staff");

        init();

        currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        etCountry.setText(str[0]);

        setImageFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_staff_edit, menu);

            if (isAdmin) {
                menu.findItem(R.id.menuMakeAdmin).setVisible(true);
                if (isPost) {
                    menu.findItem(R.id.menuMakeAdmin).setTitle("Remove from admin");
                } else {
                    menu.findItem(R.id.menuMakeAdmin).setTitle("Make as admin");
                }

            } else {
                menu.findItem(R.id.menuMakeAdmin).setVisible(false);
            }
        }
        mainMenu = menu;
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

            SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to permanently delete this staff.?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteStaff(AddStaffActivity.this, GroupDashboardActivityNew.groupId, studentData.getUserId());
                }
            });
            return true;
        }
        if (item.getItemId() == R.id.menuMakeAdmin) {

            SMBDialogUtils.showSMBDialogOKCancel(this, isPost ? "Are you sure you want to remove this users from admin?" : "Are you sure you want to make this user as admin?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (isPost) {
                        leafManager.notAllowPost(AddStaffActivity.this, group_id + "", studentData.userId + "");
                    } else {
                        leafManager.allowPost(AddStaffActivity.this, group_id + "", studentData.userId);
                    }
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {
        leafManager = new LeafManager();
        group_id = getIntent().getStringExtra("group_id");
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        isPost = getIntent().getBooleanExtra("isPost", false);
        AppLog.e(TAG, "group_id : " + group_id);
        isEdit = getIntent().getBooleanExtra("isEdit", false);

        String[] bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        String[] genderArray = getResources().getStringArray(R.array.gender_array);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
        etGender.setAdapter(genderAdapter);

        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        etBlood.setAdapter(bloodGrpAdapter);


        if (isEdit) {
            studentData = new Gson().fromJson(getIntent().getStringExtra("staff_data"), StaffResponse.StaffData.class);
            etName.setText(studentData.name);
            etPhone.setText(studentData.phone);
            etStudentId.setText(studentData.staffId);
            etDesig.setText(studentData.designation);
            etClass.setText(studentData.className);
            etQuali.setText(studentData.qualification);
            etdoj.setText(studentData.DOJ);
            etReligion.setText(studentData.religion);
            etCast.setText(studentData.caste);
            etEmail.setText(studentData.email);
            etAddress.setText(studentData.address);
            etPhone.setEnabled(false);


            for (int i = 0; i < bloodGrpArray.length; i++) {
                if (bloodGrpArray[i].equals(studentData.bloodGroup)) {
                    etBlood.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < genderArray.length; i++) {
                if (genderArray[i].equals(studentData.gender)) {
                    etGender.setSelection(i);
                    break;
                }
            }

        }

    }

    private void setImageFragment() {
        if (isEdit) {
            imageFragment = UploadImageFragment.newInstance(studentData.getImage(), true, true);
            btnAdd.setText("Update");
            setTitle("Staff Detail");
        } else {
            imageFragment = UploadImageFragment.newInstance(null, true, true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private long lastClickTime = 0;

    @OnClick({R.id.btnAdd, R.id.etCountry, R.id.etdob, R.id.etdoj})
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
                if (isValid()) {
                    StaffResponse.StaffData addStudentReq = new StaffResponse.StaffData();
                    addStudentReq.name = etName.getText().toString();
                    String[] str = getResources().getStringArray(R.array.array_country_values);
                    addStudentReq.countryCode = str[currentCountry - 1];

                    addStudentReq.staffId = etStudentId.getText().toString();
                    addStudentReq.designation = etDesig.getText().toString();
                    addStudentReq.className = etClass.getText().toString();
                    if (etGender.getSelectedItemPosition() > 0) {
                        addStudentReq.gender = etGender.getSelectedItem().toString();
                    } else {
                        addStudentReq.gender = "";
                    }
                    addStudentReq.DOJ = etdob.getText().toString();
                    addStudentReq.qualification = etQuali.getText().toString();
                    addStudentReq.religion = etReligion.getText().toString();
                    addStudentReq.caste = etCast.getText().toString();
                    if (etBlood.getSelectedItemPosition() > 0) {
                        addStudentReq.bloodGroup = etBlood.getSelectedItem().toString();
                    } else {
                        addStudentReq.bloodGroup = "";
                    }
                    addStudentReq.email = etEmail.getText().toString();
                    addStudentReq.address = etAddress.getText().toString();

                    if (isEdit) {
                        if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                            addStudentReq.image = null;
                        } else {
                            addStudentReq.image = imageFragment.getmProfileImage();
                        }
                        addStudentReq.phone = null;
                        AppLog.e(TAG, "send data update : " + new Gson().toJson(addStudentReq));
                        progressBar.setVisibility(View.VISIBLE);
                        leafManager.editStaff(this, group_id, studentData.getUserId(), addStudentReq);
                    } else {
                        addStudentReq.phone = etPhone.getText().toString();
                        addStudentReq.image = imageFragment.getmProfileImage();
                        AppLog.e(TAG, "send data : " + new Gson().toJson(addStudentReq));
                        progressBar.setVisibility(View.VISIBLE);
                        leafManager.addStaff(this, group_id, addStudentReq);
                    }
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
            case R.id.etdoj:
                DatePickerFragment fragment2 = DatePickerFragment.newInstance();
                fragment2.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etdoj.setText(format.format(c.getTime()));
                    }
                });
                fragment2.show(getSupportFragmentManager(), "datepicker");
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
        } else if (currentCountry != 1) {
            return isValueValid(etEmail);
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
            case LeafManager.API_STAFF_ADD:
                Toast.makeText(this, "Add Staff successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case LeafManager.API_STAFF_EDIT:
                Toast.makeText(this, "Edit Staff successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case LeafManager.API_STAFF_DELETE:
                Toast.makeText(this, "Delete Staff successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;

            case LeafManager.API_ALLOW_POST:
            case LeafManager.API_NOT_ALLOW_POST:
                isPost = !isPost;
                if (mainMenu != null) {
                    if (isPost) {
                        mainMenu.findItem(R.id.menuMakeAdmin).setTitle("Remove from admin");
                    } else {
                        mainMenu.findItem(R.id.menuMakeAdmin).setTitle("Make as admin");
                    }
                }
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
