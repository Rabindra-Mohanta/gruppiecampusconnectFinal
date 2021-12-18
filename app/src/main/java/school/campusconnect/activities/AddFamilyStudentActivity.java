package school.campusconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothMemberReq;
import school.campusconnect.datamodel.family.FamilyMemberResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.views.SMBDialogUtils;

public class AddFamilyStudentActivity extends BaseActivity {
    private static final String TAG = "AddStudentActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etPhone)
    public EditText etPhone;

    @Bind(R.id.etRelationShip)
    public EditText etRelationShip;
    @Bind(R.id.etAddress)
    public EditText etAddress;


    @Bind(R.id.etdob)
    public EditText etdob;

    @Bind(R.id.etGender)
    public Spinner etGender;

    @Bind(R.id.etBlood)
    public Spinner etBlood;

    @Bind(R.id.etVoterId)
    public EditText etVoterId;

    @Bind(R.id.etAadhar)
    public EditText etAadhar;

    @Bind(R.id.btnAdd)
    public Button btnAdd;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;

    int pos = -1;
    ArrayList<FamilyMemberResponse.FamilyMemberData> list;
    private boolean isDelete=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_student);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        init();
    }
    String[] bloodGrpArray;
    String[] genderArray;
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> bloodGrpAdapter;
    private void init() {
        leafManager = new LeafManager();
        pos = getIntent().getIntExtra("pos",-1);
        list = new Gson().fromJson(getIntent().getStringExtra("data"), new TypeToken<ArrayList<FamilyMemberResponse.FamilyMemberData>>() {
        }.getType());

        bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        genderArray = getResources().getStringArray(R.array.gender_array);

        genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
        etGender.setAdapter(genderAdapter);

        bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        etBlood.setAdapter(bloodGrpAdapter);

        if(pos!=-1){
            setTitle("Update Family Member");
            btnAdd.setText("Update");
            setData(list.get(pos));
        }else {
            setTitle("Add Family Member");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(pos!=-1){
            getMenuInflater().inflate(R.menu.menu_edit,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menuDelete){
            if (list == null)
                return true;

            SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to delete.?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    list.remove(pos);
                    progressBar.setVisibility(View.VISIBLE);
                    FamilyMemberResponse req = new FamilyMemberResponse();
                    req.setData(list);
                    AppLog.e(TAG,"req : "+req);
                    isDelete = true;
                    leafManager.addFamilyMember(AddFamilyStudentActivity.this, GroupDashboardActivityNew.groupId, LeafPreference.getInstance(AddFamilyStudentActivity.this).getUserId(),req);
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setData(FamilyMemberResponse.FamilyMemberData item) {

        etName.setText(item.name);
        etRelationShip.setText(item.relationship);
        etAddress.setText(item.address);
        etPhone.setText(item.phone);

        if("self".equalsIgnoreCase(item.relationship)){
            etRelationShip.setEnabled(false);
        }else {
            etRelationShip.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if("self".equalsIgnoreCase(etRelationShip.getText().toString())){
                        etRelationShip.setText("");
                    }
                }
            });
        }

        int index = 0;
        for (int i=0;i<genderArray.length;i++){
            if((item.gender+"").equals(genderArray[i])){
                index = i;
            }
        }
        etGender.setSelection(index);

        index = 0;
        for (int i=0;i<bloodGrpArray.length;i++){
            if((item.bloodGroup+"").equals(bloodGrpArray[i])){
                index = i;
            }
        }
        etBlood.setSelection(index);
    }

    private long lastClickTime = 0;

    @OnClick({R.id.btnAdd,R.id.etdob})
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
                hide_keyboard(view);

                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    Toast.makeText(AddFamilyStudentActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etRelationShip.getText().toString().trim())) {
                    Toast.makeText(AddFamilyStudentActivity.this, "Please Enter Relationship", Toast.LENGTH_SHORT).show();
                } else {
                    FamilyMemberResponse.FamilyMemberData item;
                    if(pos!=-1){
                        item = list.get(pos);
                    }else {
                        item =new FamilyMemberResponse.FamilyMemberData();
                        list.add(item);
                    }
                    item.name = etName.getText().toString();
                    item.countryCode = "IN";
                    item.relationship = etRelationShip.getText().toString();
                    item.address = etAddress.getText().toString();
                    item.phone = etPhone.getText().toString();
                    item.dob = etdob.getText().toString();
                    if (etGender.getSelectedItemPosition() > 0) {
                        item.gender = etGender.getSelectedItem().toString();
                    } else {
                        item.gender = "";
                    }
                    if (etBlood.getSelectedItemPosition() > 0) {
                        item.bloodGroup = etBlood.getSelectedItem().toString();
                    } else {
                        item.bloodGroup = "";
                    }
                    item.voterId = etVoterId.getText().toString();
                    item.aadharNumber = etAadhar.getText().toString();

                    progressBar.setVisibility(View.VISIBLE);
                    FamilyMemberResponse req = new FamilyMemberResponse();
                    req.setData(list);
                    AppLog.e(TAG,"req : "+req);
                    leafManager.addFamilyMember(this, GroupDashboardActivityNew.groupId, LeafPreference.getInstance(this).getUserId(),req);
                }
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
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_CREATE_FAMILY_MEMBER:
                if(isDelete){
                    Toast.makeText(this, "Delete Family Member successfully", Toast.LENGTH_SHORT).show();
                }else if(pos!=-1){
                    Toast.makeText(this, "Update Family Member successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Delete Family Member successfully", Toast.LENGTH_SHORT).show();
                }

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
