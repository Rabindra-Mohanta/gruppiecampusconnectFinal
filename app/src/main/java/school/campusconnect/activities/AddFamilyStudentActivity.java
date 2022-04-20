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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import school.campusconnect.datamodel.profile.ProfileItem;
import school.campusconnect.datamodel.profileCaste.CasteResponse;
import school.campusconnect.datamodel.profileCaste.ReligionResponse;
import school.campusconnect.datamodel.profileCaste.SubCasteResponse;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.fragments.ProfileFragmentConst;
import school.campusconnect.fragments.SearchCastFragmentDialog;
import school.campusconnect.fragments.SearchSubCasteDialogFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.UploadCircleImageFragment;
import school.campusconnect.views.SMBDialogUtils;

public class AddFamilyStudentActivity extends BaseActivity implements SearchCastFragmentDialog.SelectListener,SearchSubCasteDialogFragment.SelectListener {
    private static final String TAG = "AddStudentActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etPhone)
    public EditText etPhone;

    @Bind(R.id.etRelationShip)
    public EditText etRelationShip;
/*    @Bind(R.id.etAddress)
    public EditText etAddress;*/


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

    @Bind(R.id.etEmail)
    public EditText etEmail;

    boolean isEdit = false;

    int indexGender,indexBlood;
    @Bind(R.id.etProfession)
    public EditText etProfession;

    @Bind(R.id.etEducation)
    public EditText etEducation;

    @Bind(R.id.etCategory)
    public EditText etCategory;


    @Bind(R.id.etCaste)
    public TextView etCaste;

    @Bind(R.id.etSubCaste)
    public TextView etSubCaste;

    @Bind(R.id.etReligion)
    public Spinner etReligion;


    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;

    String[] bloodGrpArray;
    String[] genderArray;

    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> bloodGrpAdapter;
    ArrayAdapter<String> religionAdapter;

    ArrayList<String> religionList = new ArrayList<>();
    ArrayList<String> casteList = new ArrayList<>();
    ArrayList<String> subCasteList = new ArrayList<>();
    ArrayList<CasteResponse.CasteData> casteDataList = new ArrayList<>();

    boolean isFirstTimeReligion = true;
    boolean isReligionUpdate = false;
    boolean isFirstTimeCaste = true;
    boolean isFirstTimeSubCaste = true;

    String casteId = null;

    String religion = null;
    String caste = null;
    String subcaste = null;


    SearchCastFragmentDialog searchCastFragmentDialog;
    SearchSubCasteDialogFragment searchSubCasteDialogFragment;

    public static String profileImage;

    public boolean isCasteClickable = false;

    UploadCircleImageFragment imageFragment;

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

    private void init() {
        leafManager = new LeafManager();
        pos = getIntent().getIntExtra("pos",-1);
        list = new Gson().fromJson(getIntent().getStringExtra("data"), new TypeToken<ArrayList<FamilyMemberResponse.FamilyMemberData>>() {
        }.getType());


        profileImage = "";

        searchCastFragmentDialog = SearchCastFragmentDialog.newInstance();
        searchCastFragmentDialog.setListener(this);

        searchSubCasteDialogFragment = SearchSubCasteDialogFragment.newInstance();
        searchSubCasteDialogFragment.setListener(this);

        bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        genderArray = getResources().getStringArray(R.array.gender_array);

        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();


        etCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCasteClickable)
                {
                    searchCastFragmentDialog.show(getSupportFragmentManager(),"");
                }
            }
        });

        etSubCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCasteClickable)
                {
                    searchSubCasteDialogFragment.show(getSupportFragmentManager(),"");
                }

            }
        });




        etReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0)
                {
                    isCasteClickable = true;
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.getCaste(AddFamilyStudentActivity.this,etReligion.getSelectedItem().toString());
                }
                else
                {
                    isCasteClickable = false;
                    etCaste.setText("");
                    etSubCaste.setText("");
                    etCategory.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

            SMBDialogUtils.showSMBDialogOKCancel(this, getResources().getString(R.string.dialog_are_you_want_to_delete), new DialogInterface.OnClickListener() {
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

    @Override
    protected void onStart() {
        super.onStart();

        if(pos!=-1){
            isReligionUpdate = true;
            setTitle(getResources().getString(R.string.title_update_family_member));
            setData(list.get(pos));
        }else {

            isEdit = true;
            isFirstTimeReligion = false;
            isFirstTimeCaste = false;
            isFirstTimeSubCaste = false;
            isCasteClickable = true;
            progressBar.setVisibility(View.VISIBLE);
            leafManager.getReligion(this);

            genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
            etGender.setAdapter(genderAdapter);

            bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
            etBlood.setAdapter(bloodGrpAdapter);
            setTitle(getResources().getString(R.string.title_add_family_member));
        }
    }

    private void setData(FamilyMemberResponse.FamilyMemberData item) {

        AppLog.e(TAG,"FamilyMemberData "+new Gson().toJson(item));

        genderAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner_disable, R.id.tvItem, genderArray);
        etGender.setAdapter(genderAdapter);
        etGender.setEnabled(false);

        bloodGrpAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner_disable, R.id.tvItem, bloodGrpArray);
        etBlood.setAdapter(bloodGrpAdapter);
        etBlood.setEnabled(false);

        etName.setEnabled(false);
        etRelationShip.setTextColor(getResources().getColor(R.color.grey));

        etRelationShip.setEnabled(false);
        etName.setTextColor(getResources().getColor(R.color.grey));

        etPhone.setEnabled(false);
        etPhone.setTextColor(getResources().getColor(R.color.grey));

        etVoterId.setEnabled(false);
        etVoterId.setTextColor(getResources().getColor(R.color.grey));

        etEmail.setEnabled(false);
        etEmail.setTextColor(getResources().getColor(R.color.grey));

        etdob.setEnabled(false);
        etdob.setTextColor(getResources().getColor(R.color.grey));

        etEducation.setEnabled(false);
        etEducation.setTextColor(getResources().getColor(R.color.grey));

        etProfession.setEnabled(false);
        etProfession.setTextColor(getResources().getColor(R.color.grey));

        etCategory.setTextColor(getResources().getColor(R.color.grey));

        religion = item.religion;
        caste = item.caste;
        subcaste = item.subcaste;
        profileImage = item.image;

        etName.setText(item.name);
        etRelationShip.setText(item.relationship);
        etEmail.setText(item.email);
        etPhone.setText(item.phone);
        etEducation.setText(item.qualification);
        etProfession.setText(item.designation);
        etVoterId.setText(item.voterId);
        etdob.setText(item.dob);

        int index = 0;
        for (int i = 0; i < genderArray.length; i++) {
            if ((item.gender + "").equals(genderArray[i])) {
                index = i;
            }
        }
        indexGender = index;
        etGender.setSelection(index);

        int index1 = 0;
        for (int i = 0; i < bloodGrpArray.length; i++) {
            if ((item.bloodGroup + "").equals(bloodGrpArray[i])) {
                index1 = i;
            }
        }
        indexBlood = index1;
        etBlood.setSelection(index1);

        if (item.image != null && !item.image.isEmpty() && Constants.decodeUrlToBase64(item.image).contains("http")) {
            imageFragment.updatePhotoFromUrl(item.image);
        } else if (item.image == null) {
            Log.e("ProfileActivity", "image is Null From API "+item.name);
            imageFragment.setInitialLatterImage(item.name);
        }

        progressBar.setVisibility(View.VISIBLE);
        leafManager.getReligion(this);
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

                if (!isEdit)
                {
                    isEdit = true;

                    etName.setEnabled(true);
                    etName.setTextColor(getResources().getColor(R.color.white));

                    etRelationShip.setEnabled(true);
                    etRelationShip.setTextColor(getResources().getColor(R.color.white));

                    etPhone.setEnabled(true);
                    etPhone.setTextColor(getResources().getColor(R.color.white));

                    etVoterId.setEnabled(true);
                    etVoterId.setTextColor(getResources().getColor(R.color.white));

                    etEmail.setEnabled(true);
                    etEmail.setTextColor(getResources().getColor(R.color.white));

                    etdob.setEnabled(true);
                    etdob.setTextColor(getResources().getColor(R.color.white));

                    etEducation.setEnabled(true);
                    etEducation.setTextColor(getResources().getColor(R.color.white));

                    etProfession.setEnabled(true);
                    etProfession.setTextColor(getResources().getColor(R.color.white));

                    etCategory.setTextColor(getResources().getColor(R.color.white));
                    etCaste.setTextColor(getResources().getColor(R.color.white));
                    etSubCaste.setTextColor(getResources().getColor(R.color.white));

                    genderAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, R.id.tvItem, genderArray);
                    etGender.setAdapter(genderAdapter);
                    etGender.setEnabled(true);

                    bloodGrpAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
                    etBlood.setAdapter(bloodGrpAdapter);
                    etBlood.setEnabled(true);

                    etGender.setSelection(indexGender);
                    etBlood.setSelection(indexBlood);

                    religionAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, R.id.tvItem, religionList);
                    etReligion.setAdapter(religionAdapter);
                    etReligion.setEnabled(true);
                    etReligion.setSelection(religionAdapter.getPosition(religion));


                    if("self".equalsIgnoreCase(etRelationShip.getText().toString())){
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

                    if(pos!=-1){
                        btnAdd.setText("Update");
                    }else {
                        btnAdd.setText("Save");
                    }

                    return;
                }


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
                 //   item.address = etAddress.getText().toString();
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

                    item.email = etEmail.getText().toString();

                    item.qualification = etEducation.getText().toString();
                    item.caste = etCaste.getText().toString();
                    item.subcaste = etSubCaste.getText().toString();
                    item.religion = etReligion.getSelectedItem().toString();
                    item.designation = etProfession.getText().toString();

                    item.image = imageFragment.getmProfileImage();

                    progressBar.setVisibility(View.VISIBLE);
                    FamilyMemberResponse req = new FamilyMemberResponse();
                    req.setData(list);
                    AppLog.e(TAG,"req : "+new Gson().toJson(req));
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

            case LeafManager.API_RELIGION_GET:
                ReligionResponse res = (ReligionResponse) response;

                AppLog.e(TAG, "ReligionResponse" + res);

                religionList.clear();
                religionList.add(0,"select religion");
                religionList.addAll(res.getReligionData().get(0).getReligionList());

                if (res.getReligionData().size() > 0)
                {
                    religionAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner_disable, R.id.tvItem, religionList);
                    etReligion.setAdapter(religionAdapter);
                }
                if (isFirstTimeReligion)
                {
                    isFirstTimeReligion = false;
                    etReligion.setEnabled(false);

                    if (religion != null)
                    {
                        etReligion.setSelection(religionAdapter.getPosition(religion));
                    }
                    else
                    {
                        etReligion.setSelection(religionAdapter.getPosition("select religion"));
                    }

                }

                if (!isReligionUpdate)
                {
                    isReligionUpdate = true;
                    religionAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, R.id.tvItem, religionList);
                    etReligion.setAdapter(religionAdapter);
                }


                break;

            case LeafManager.API_CASTE_GET:
                CasteResponse response1 = (CasteResponse) response;

                AppLog.e(TAG, "CasteResponse" + response1);



                casteDataList.clear();
                casteDataList.addAll(response1.getCasteData());
                casteList.clear();

                for (int i=0;i<response1.getCasteData().size();i++)
                {
                    casteList.add(response1.getCasteData().get(i).getCasteName());
                }

                if (casteList.size() > 0)
                {
                    if (isFirstTimeCaste)
                    {
                        etCaste.setTextColor(getResources().getColor(R.color.grey));

                        if (caste != null)
                        {
                            etCaste.setText(caste);
                        }
                        else
                        {
                            etCaste.setText(response1.getCasteData().get(0).getCasteName());
                        }
                        for (int i=0;i<casteDataList.size();i++)
                        {
                            if (etCaste.getText().toString().toLowerCase().trim().equalsIgnoreCase(casteDataList.get(i).getCasteName().toLowerCase().trim()))
                            {
                                casteId = casteDataList.get(i).getCasteId();
                                etCategory.setText(casteDataList.get(i).getCategoryName());
                            }
                        }

                        if (isEdit)
                        {
                            isFirstTimeCaste = false;
                            etCaste.setTextColor(getResources().getColor(R.color.white));
                            etCaste.setEnabled(true);
                        }
                        else {
                            etCaste.setTextColor(getResources().getColor(R.color.grey));
                            etCaste.setEnabled(false);
                        }
                    }
                    else
                    {
                        casteId = response1.getCasteData().get(0).getCasteId();
                        etCaste.setText(response1.getCasteData().get(0).getCasteName());
                        etCategory.setText(response1.getCasteData().get(0).getCategoryName());
                    }

                    searchCastFragmentDialog.setData(response1.getCasteData());

                }

                if (casteId != null)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.getSubCaste(this,casteId);
                }

                break;

            case LeafManager.API_SUB_CASTE_GET:
                SubCasteResponse response2 = (SubCasteResponse) response;

                AppLog.e(TAG, "SubCasteResponse" + response2);

                casteId = null;

                subCasteList.clear();

                for (int i=0;i<response2.getSubCasteData().size();i++)
                {
                    subCasteList.add(response2.getSubCasteData().get(i).getSubCasteName());
                }


                if (subCasteList.size() > 0)
                {

                    if (isFirstTimeSubCaste)
                    {
                        etSubCaste.setTextColor(getResources().getColor(R.color.grey));

                        if (subcaste != null)
                        {
                            etSubCaste.setText(subcaste);
                        }
                        else
                        {
                            etSubCaste.setText(response2.getSubCasteData().get(0).getSubCasteName());
                        }


                        if (isEdit)
                        {
                            isFirstTimeSubCaste = false;
                            etSubCaste.setTextColor(getResources().getColor(R.color.white));
                            etSubCaste.setEnabled(true);
                        }
                        else {
                            etSubCaste.setTextColor(getResources().getColor(R.color.grey));
                            etSubCaste.setEnabled(false);
                        }
                    }
                    else
                    {
                        etSubCaste.setText(response2.getSubCasteData().get(0).getSubCasteName());
                    }

                    searchSubCasteDialogFragment.setData(response2.getSubCasteData());
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


    @Override
    public void onSelected(CasteResponse.CasteData casteData) {

        etCaste.setText(casteData.getCasteName());
        etCategory.setText(casteData.getCategoryName());

        casteId = casteData.getCasteId();

        if (casteId != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            leafManager.getSubCaste(this,casteId);
        }
    }

    @Override
    public void onSelected(SubCasteResponse.SubCasteData casteData) {

        etSubCaste.setText(casteData.getSubCasteName());

    }
}
