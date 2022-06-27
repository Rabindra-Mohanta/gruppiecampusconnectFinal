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
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class AddClassStudentActivity extends BaseActivity {
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

    @Bind(R.id.etRollNo)
    public EditText etRollNo;
    @Bind(R.id.etStudentId)
    public EditText etStudentId;
    @Bind(R.id.etAdmissionNumber)
    public EditText etAdmissionNumber;
    @Bind(R.id.etClass)
    public EditText etClass;
    @Bind(R.id.etsection)
    public EditText etsection;

    @Bind(R.id.etAadhar)
    public EditText etAadhar;

    @Bind(R.id.etReligion)
    public EditText etReligion;

    @Bind(R.id.etCast)
    public EditText etCast;

    @Bind(R.id.etBlood)
    public Spinner etBlood;

    @Bind(R.id.etdob)
    public EditText etdob;
    @Bind(R.id.etdoj)
    public EditText etdoj;
    @Bind(R.id.etfatherName)
    public EditText etfatherName;
    @Bind(R.id.etmotherName)
    public EditText etmotherName;
    @Bind(R.id.etfatherNumber)
    public EditText etfatherNumber;
    @Bind(R.id.etmotherNumber)
    public EditText etmotherNumber;
    @Bind(R.id.etEmail)
    public EditText etEmail;
    @Bind(R.id.etAddress)
    public EditText etAddress;
    @Bind(R.id.labelPhone)
    public TextView labelPhone;


    @Bind(R.id.etNationlity)
    public EditText etNationlity;

    @Bind(R.id.etFatherOccupation)
    public EditText etFatherOccupation;

    @Bind(R.id.etFatherEducation)
    public EditText etFatherEducation;

    @Bind(R.id.etmotherOccupation)
    public EditText etmotherOccupation;

    @Bind(R.id.etmotherEducation)
    public EditText etmotherEducation;

    @Bind(R.id.etCategory)
    public EditText etCategory;

    @Bind(R.id.etDisability)
    public Spinner etDisability;

    private int currentCountry;

    LeafManager leafManager;

    private UploadImageFragment imageFragment;

    String group_id, team_id,userId,gruppieRollNoNumber;
    private boolean isEdit=false;

    StudentRes.StudentData studentData;

    boolean submitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class_student);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_student));

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
                    showLoadingBar(progressBar,false);
                 //   progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteClassStudent(AddClassStudentActivity.this, GroupDashboardActivityNew.groupId, team_id,studentData.getUserId());
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

        String[] bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        etBlood.setAdapter(bloodGrpAdapter);

        String[] disabilityArray = getResources().getStringArray(R.array.disability);
        ArrayAdapter<String> disabilityAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, disabilityArray);
        etDisability.setAdapter(disabilityAdapter);

        if(isEdit){

            studentData = new Gson().fromJson(getIntent().getStringExtra("student_data"), StudentRes.StudentData.class);

            userId = studentData.getUserId();
            gruppieRollNoNumber = studentData.getGruppieRollNumber();

            Log.e(TAG,"student Data"+new Gson().toJson(studentData));
            etName.setText(studentData.name);

            etPhone.setText(studentData.phone);
            etAadhar.setText(studentData.aadharNumber);
            etReligion.setText(studentData.religion);
            etCast.setText(studentData.caste);
            etStudentId.setText(studentData.studentId);
            etAdmissionNumber.setText(studentData.admissionNumber);
            etRollNo.setText(studentData.rollNumber);
            etClass.setText(studentData._class);
            etsection.setText(studentData.section);
            etdob.setText(studentData.dob);
            etdoj.setText(studentData.doj);
            etfatherName.setText(studentData.fatherName);
            etmotherName.setText(studentData.motherName);
            etfatherNumber.setText(studentData.fatherNumber);
            etmotherNumber.setText(studentData.motherNumber);
            etEmail.setText(studentData.email);
            etAddress.setText(studentData.address);

            etmotherEducation.setText(studentData.getMotherEducation());
            etmotherOccupation.setText(studentData.getMotherOccupation());
            etFatherEducation.setText(studentData.getFatherEducation());
            etFatherOccupation.setText(studentData.getFatherOccupation());
            etCategory.setText(studentData.getCategory());
            etNationlity.setText(studentData.getNationality());

            etPhone.setEnabled(true);

            for (int i = 0; i < bloodGrpArray.length; i++) {
                if (bloodGrpArray[i].equals(studentData.bloodGroup)) {
                    etBlood.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < disabilityArray.length; i++) {
                if (disabilityArray[i].equals(studentData.getDisability())) {
                    etDisability.setSelection(i);
                    break;
                }
            }
        }

    }

    private void setImageFragment() {
        if (isEdit) {
            imageFragment = UploadImageFragment.newInstance(studentData.getImage(), true, true);
            btnAdd.setText(getResources().getString(R.string.lbl_update));
            setTitle(getResources().getString(R.string.title_student_details)+" - ("+getIntent().getStringExtra("className")+")");
        } else {
            imageFragment = UploadImageFragment.newInstance(null, true, true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }
    private long lastClickTime = 0;
    @OnClick({R.id.btnAdd, R.id.etCountry,R.id.etdob,R.id.etdoj})
    public void onClick(View view) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
        }else {
            return;
        }
        Log.e(TAG,"Tap : ");
        switch (view.getId()) {
            case R.id.btnAdd:

                if (isValid() && !submitted )
                {
                    submitted = true;
                    StudentRes.StudentData addStudentReq = new StudentRes.StudentData();
                    addStudentReq.name = etName.getText().toString();
                    String[] str = getResources().getStringArray(R.array.array_country_values);
                    addStudentReq.countryCode = str[currentCountry - 1];

                    addStudentReq.studentId = etStudentId.getText().toString();
                    addStudentReq.admissionNumber = etAdmissionNumber.getText().toString();
                    addStudentReq.rollNumber = etRollNo.getText().toString();
                    addStudentReq._class = etClass.getText().toString();
                    addStudentReq.aadharNumber = etAadhar.getText().toString();
                    addStudentReq.religion = etReligion.getText().toString();
                    addStudentReq.caste = etCast.getText().toString();
                    addStudentReq.section = etsection.getText().toString();
                    addStudentReq.dob = etdob.getText().toString();
                    addStudentReq.doj = etdoj.getText().toString();
                    addStudentReq.fatherName = etfatherName.getText().toString();
                    addStudentReq.motherName= etmotherName.getText().toString();
                    addStudentReq.fatherNumber= etfatherNumber.getText().toString();
                    addStudentReq.motherNumber= etmotherNumber.getText().toString();
                    addStudentReq.email= etEmail.getText().toString();
                    addStudentReq.address= etAddress.getText().toString();
                    addStudentReq.setCategory(etCategory.getText().toString());

                    if (etBlood.getSelectedItemPosition() != 0)
                    {
                        addStudentReq.bloodGroup = etBlood.getSelectedItem().toString();
                    }

                    if (etDisability.getSelectedItemPosition() != 0)
                    {
                        addStudentReq.setDisability(etDisability.getSelectedItem().toString());
                    }

                    addStudentReq.setFatherEducation(etFatherEducation.getText().toString());
                    addStudentReq.setFatherOccupation(etFatherOccupation.getText().toString());
                    addStudentReq.setMotherEducation(etmotherEducation.getText().toString());
                    addStudentReq.setMotherOccupation(etmotherOccupation.getText().toString());
                    addStudentReq.setNationality(etNationlity.getText().toString().toUpperCase());
                    addStudentReq.setUserId(studentData.getUserId());
                    addStudentReq.setGruppieRollNumber(studentData.getGruppieRollNumber());

                    if(isEdit){
                        if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                            addStudentReq.image=null;
                        }
                        else
                        {
                            addStudentReq.image=imageFragment.getmProfileImage();
                        }

                        addStudentReq.phone = null;
                        AppLog.e(TAG, "send data : " + new Gson().toJson(addStudentReq));
                        showLoadingBar(progressBar,false);
                        //   progressBar.setVisibility(View.VISIBLE);
                        leafManager.editClassStudent(this, group_id, team_id,studentData.getUserId(),gruppieRollNoNumber, addStudentReq);
                    }
                    else {
                        addStudentReq.phone = etPhone.getText().toString();
                        addStudentReq.image=imageFragment.getmProfileImage();
                        AppLog.e(TAG, "send data : " + addStudentReq);
                        showLoadingBar(progressBar,false);
                        //   progressBar.setVisibility(View.VISIBLE);
                        leafManager.addClassStudent(this, group_id, team_id, addStudentReq);
                    }
                }
                else
                {
                    submitted =false;
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
        }
        else if(!isValueValid(etRollNo)){
            return false;
        }
        else if(currentCountry!=1){
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
        submitted = false;
        if (progressBar != null)
            hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_ADD_ClASS_STUDENTS:
                Toast.makeText(this, getResources().getString(R.string.toast_add_student_sucess), Toast.LENGTH_SHORT).show();
                finish();
                break;
            case LeafManager.API_EDIT_STUDENTS:
                StudentRes.StudentData addStudentReq = new StudentRes.StudentData();

                String phone = "";

                if (!etPhone.getText().toString().contains("+91"))
                {
                    phone = "+91"+etPhone.getText().toString();
                }
                else
                {
                    phone = etPhone.getText().toString();
                }

                Log.e(TAG,"phone "+phone);

                if (!studentData.getPhone().toString().equalsIgnoreCase(phone))
                {
                    String[] str = getResources().getStringArray(R.array.array_country_values);
                    addStudentReq.countryCode = str[currentCountry - 1];
                    addStudentReq.phone = etPhone.getText().toString();
                    showLoadingBar(progressBar,false);
                    //progressBar.setVisibility(View.VISIBLE);
                    leafManager.editClassStudentPhone(this, group_id,studentData.getUserId(), addStudentReq);
                }
                else
                {
                    Toast.makeText(this, getResources().getString(R.string.toast_edit_student_sucess), Toast.LENGTH_SHORT).show();
                    finish();
                }

               /* Toast.makeText(this, "Edit Student successfully", Toast.LENGTH_SHORT).show();
                finish();*/
                break;

            case LeafManager.API_UPDATE_PHONE_STUDENT:
                Toast.makeText(this, getResources().getString(R.string.toast_edit_student_sucess), Toast.LENGTH_SHORT).show();
                finish();
                break;

            case LeafManager.API_DELETE_STUDENTS:
                Toast.makeText(this, getResources().getString(R.string.toast_delete_student_sucess), Toast.LENGTH_SHORT).show();
                finish();
                break;

        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        super.onFailure(apiId, msg);
        submitted =false;
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

        submitted =false;
        if (progressBar != null)
            hideLoadingBar();
        //    progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
