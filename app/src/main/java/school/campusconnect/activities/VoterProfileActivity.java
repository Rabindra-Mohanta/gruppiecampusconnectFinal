package school.campusconnect.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityVoterProfileBinding;
import school.campusconnect.datamodel.AddPostValidationError;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.booths.BoothVotersListResponse;
import school.campusconnect.datamodel.booths.VoterProfileResponse;
import school.campusconnect.datamodel.booths.VoterProfileUpdate;
import school.campusconnect.datamodel.profileCaste.CasteResponse;
import school.campusconnect.datamodel.profileCaste.ReligionResponse;
import school.campusconnect.datamodel.profileCaste.SubCasteResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.fragments.SearchCastFragmentDialog;
import school.campusconnect.fragments.SearchSubCasteDialogFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.UploadCircleImageFragment;

public class VoterProfileActivity extends BaseActivity implements LeafManager.OnCommunicationListener,SearchCastFragmentDialog.SelectListener,SearchSubCasteDialogFragment.SelectListener{

    public static String TAG = "VoterProfileActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    ActivityVoterProfileBinding binding;
    BoothVotersListResponse.VoterData data;

    UploadCircleImageFragment imageFragment;

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
    boolean isFirstTimeCaste = true;
    boolean isFirstTimeSubCaste = true;

    String casteId = null;

    private boolean isCommittee = false;
    int apiCount = 0;

    String religion = null;
    String caste = null;
    String subcaste = null;

    SearchCastFragmentDialog searchCastFragmentDialog;
    SearchSubCasteDialogFragment searchSubCasteDialogFragment;

    LeafManager manager;

    String userID;
    String teamID;

    int indexGender,indexBlood;
    public static String profileImage;

    boolean isEdit = false;

    boolean isCasteClickable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_voter_profile);

        inits();

        getProfileData();

    }

    private void getProfileData() {

        if (isConnectionAvailable())
        {
            showLoadingBar(binding.progressBar);
           // binding.progressBar.setVisibility(View.VISIBLE);
            manager.getVoterProfile(this,GroupDashboardActivityNew.groupId,userID);
        }
        else
        {
            showNoNetworkMsg();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_make_app_admin, menu);

        if(GroupDashboardActivityNew.makeAdmin)
        {
            menu.findItem(R.id.menu_make_admin).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.menu_make_admin).setVisible(false);
        }

        if(GroupDashboardActivityNew.isPost)
        {
            if (isCommittee)
            {
                menu.findItem(R.id.menu_delete).setVisible(true);
            }
            else
            {
                menu.findItem(R.id.menu_delete).setVisible(false);
            }
        }
        else
        {
            menu.findItem(R.id.menu_delete).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_make_admin) {
            AppDialog.showConfirmDialog(VoterProfileActivity.this, getResources().getString(R.string.dialog_are_you_want_to_make_admin), new AppDialog.AppDialogListener() {
                @Override
                public void okPositiveClick(DialogInterface dialog) {
                    makeAppAdmin();
                }

                @Override
                public void okCancelClick(DialogInterface dialog) {

                }
            });
            return true;
        }

        if (item.getItemId() == R.id.menu_delete) {
            AppDialog.showConfirmDialog(VoterProfileActivity.this, getResources().getString(R.string.dialog_are_you_sure_want_to_delete_user), new AppDialog.AppDialogListener() {
                @Override
                public void okPositiveClick(DialogInterface dialog) {
                    deleteUser();
                }

                @Override
                public void okCancelClick(DialogInterface dialog) {

                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeAppAdmin() {

        showLoadingBar(binding.progressBar);
    //    binding.progressBar.setVisibility(View.VISIBLE);
        manager.makeAppAdmin(this,GroupDashboardActivityNew.groupId,userID);

    }

    private void deleteUser() {

        LeafManager manager = new LeafManager();
        manager.removeTeamUser(new LeafManager.OnAddUpdateListener<AddPostValidationError>() {
            @Override
            public void onSuccess(int apiId, BaseResponse response) {
                hideLoadingBar();
                //binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_user_deleted), Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                setResult(RESULT_OK,i);
                finish();
            }

            @Override
            public void onFailure(int apiId, ErrorResponseModel<AddPostValidationError> error) {
                hideLoadingBar();
                //binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),error.message,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(int apiId, String error) {
                hideLoadingBar();
                //binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
                Log.e(TAG,"onException"+error);
            }
        }, GroupDashboardActivityNew.groupId,teamID,userID);
    }

    private void inits() {


        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("name"));

        if (getIntent() != null)
        {
            userID = getIntent().getStringExtra("userID");
            isCommittee = getIntent().getBooleanExtra("committee",false);
            teamID = getIntent().getStringExtra("teamID");
        }

        if (isCommittee)
        {
            binding.etRole.setVisibility(View.VISIBLE);
            binding.lblRole.setVisibility(View.VISIBLE);
        }
        manager = new LeafManager();

        searchCastFragmentDialog = SearchCastFragmentDialog.newInstance();
        searchCastFragmentDialog.setListener(this);

        searchSubCasteDialogFragment = SearchSubCasteDialogFragment.newInstance();
        searchSubCasteDialogFragment.setListener(this);

        bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        genderArray = getResources().getStringArray(R.array.gender_array);


        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        binding.etCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCasteClickable)
                {
                    searchCastFragmentDialog.show(getSupportFragmentManager(),"");
                }


            }
        });

        binding.etSubCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCasteClickable)
                {
                    searchSubCasteDialogFragment.show(getSupportFragmentManager(),"");
                }


            }
        });

        binding.etdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        binding.etdob.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
            }
        });

         binding.etReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0)
                {
                    isCasteClickable = true;
                    showLoadingBar(binding.progressBar);
                   // binding.progressBar.setVisibility(View.VISIBLE);
                    manager.getCaste(VoterProfileActivity.this,binding.etReligion.getSelectedItem().toString());
                }
                else
                {
                    isCasteClickable = false;
                    binding.etCaste.setText("");
                    binding.etSubCaste.setText("");
                    binding.etCategory.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isEdit) {
                    isEdit = true;

                    binding.etName.setEnabled(true);
                    binding.etName.setTextColor(getResources().getColor(R.color.white));

                    binding.etPhone.setEnabled(true);
                    binding.etPhone.setTextColor(getResources().getColor(R.color.white));

                    binding.etRole.setEnabled(true);
                    binding.etRole.setTextColor(getResources().getColor(R.color.white));

                    binding.etAddress.setEnabled(true);
                    binding.etAddress.setTextColor(getResources().getColor(R.color.white));

                    binding.etVoterId.setEnabled(true);
                    binding.etVoterId.setTextColor(getResources().getColor(R.color.white));

                    binding.etEmail.setEnabled(true);
                    binding.etEmail.setTextColor(getResources().getColor(R.color.white));

                    binding.etdob.setEnabled(true);
                    binding.etdob.setTextColor(getResources().getColor(R.color.white));

                    binding.etEducation.setEnabled(true);
                    binding.etEducation.setTextColor(getResources().getColor(R.color.white));

                    binding.etProfession.setEnabled(true);
                    binding.etProfession.setTextColor(getResources().getColor(R.color.white));

                    binding.etCategory.setTextColor(getResources().getColor(R.color.white));
                    binding.etCaste.setTextColor(getResources().getColor(R.color.white));
                    binding.etSubCaste.setTextColor(getResources().getColor(R.color.white));

                    /*binding.etCaste.setEnabled(true);
                    binding.etCaste.setTextColor(getResources().getColor(R.color.white));

                    binding.etSubCaste.setEnabled(true);
                    binding.etSubCaste.setTextColor(getResources().getColor(R.color.white));

                    binding.etReligion.setEnabled(true);
                    binding.etReligion.setTextColor(getResources().getColor(R.color.white));*/

                    genderAdapter = new ArrayAdapter<String>(VoterProfileActivity.this, R.layout.item_spinner, R.id.tvItem, genderArray);
                    binding.etGender.setAdapter(genderAdapter);
                    binding.etGender.setEnabled(true);

                    bloodGrpAdapter = new ArrayAdapter<String>(VoterProfileActivity.this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
                    binding.etBlood.setAdapter(bloodGrpAdapter);
                    binding.etBlood.setEnabled(true);

                    binding.etGender.setSelection(indexGender);
                    binding.etBlood.setSelection(indexBlood);

                    religionAdapter = new ArrayAdapter<String>(VoterProfileActivity.this, R.layout.item_spinner, R.id.tvItem, religionList);
                    binding.etReligion.setAdapter(religionAdapter);
                    binding.etReligion.setEnabled(true);
                    binding.etReligion.setSelection(religionAdapter.getPosition(religion));



                   /* casteAdapter = new ArrayAdapter<String>(VoterProfileActivity.this, R.layout.item_spinner, R.id.tvItem, casteList);
                    binding.etCaste.setAdapter(casteAdapter);
                    binding.etCaste.setEnabled(true);
                    binding.etCaste.setSelection(casteAdapter.getPosition(caste));

                    subCasteAdapter = new ArrayAdapter<String>(VoterProfileActivity.this, R.layout.item_spinner, R.id.tvItem, subCasteList);
                    binding.etSubCaste.setAdapter(subCasteAdapter);
                    binding.etSubCaste.setEnabled(true);
                    binding.etSubCaste.setSelection(subCasteAdapter.getPosition(subcaste));*/

                    binding.btnAdd.setText(getResources().getString(R.string.lbl_save));
                    return;
                }

                if (isValid()) {

                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }


                    callUpdateProfileApi();
                }


            }
        });

    }
    public void callUpdateProfileApi() {

        LeafManager manager = new LeafManager();
        VoterProfileUpdate item = new VoterProfileUpdate();
        item.name = binding.etName.getText().toString();
        item.phone = binding.etPhone.getText().toString();
        item.email = binding.etEmail.getText().toString();
        item.dob = binding.etdob.getText().toString();
        item.roleOnConstituency = binding.etRole.getText().toString();
        item.qualification = binding.etEducation.getText().toString();
        item.designation = binding.etProfession.getText().toString();

        item.caste = binding.etCaste.getText().toString();
        item.subcaste = binding.etSubCaste.getText().toString();
        item.religion = binding.etReligion.getSelectedItem().toString();


        if (binding.etGender.getSelectedItemPosition() > 0) {
            item.gender = binding.etGender.getSelectedItem().toString();
        } else {
            item.gender = "";
        }
        if (binding.etBlood.getSelectedItemPosition() > 0) {
            item.bloodGroup = binding.etBlood.getSelectedItem().toString();
        } else {
            item.bloodGroup = "";
        }
        item.voterId = binding.etVoterId.getText().toString();
        item.address = binding.etAddress.getText().toString();
        item.image = imageFragment.getmProfileImage();

        Log.e(TAG,"request"+ new Gson().toJson(item));

        showLoadingBar(binding.progressBar);
        // binding.progressBar.setVisibility(View.VISIBLE);
        manager.updateProfileVoter(VoterProfileActivity.this,GroupDashboardActivityNew.groupId,userID, item);

    }


    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingBar();
       // binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_VOTER_PROFILE_GET)
        {
            VoterProfileResponse res = (VoterProfileResponse) response;

            fillDetails(res.getData());

        }

        if (apiId == LeafManager.API_MAKE_ADMIN)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_profile_update), Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            setResult(RESULT_OK,i);
            finish();
        }

        else if (LeafManager.API_RELIGION_GET == apiId)
        {
            ReligionResponse res = (ReligionResponse) response;

            AppLog.e(TAG, "ReligionResponse" + res);

            religionList.clear();
            religionList.add(0,"select religion");
            religionList.addAll(res.getReligionData().get(0).getReligionList());


            if (res.getReligionData().size() > 0)
            {
                religionAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner_disable, R.id.tvItem, religionList);
                binding.etReligion.setAdapter(religionAdapter);
            }
            if (isFirstTimeReligion)
            {
                isFirstTimeReligion = false;
                binding.etReligion.setEnabled(false);

                if (religion != null)
                {
                    binding.etReligion.setSelection(religionAdapter.getPosition(religion));
                }
                else
                {
                    binding.etReligion.setSelection(religionAdapter.getPosition("select religion"));
                }

            }

        }

        else if (LeafManager.API_CASTE_GET == apiId)
        {
            CasteResponse res = (CasteResponse) response;

            AppLog.e(TAG, "CasteResponse" + res);

            casteDataList.clear();
            casteDataList.addAll(res.getCasteData());

            casteList.clear();

            for (int i=0;i<res.getCasteData().size();i++)
            {
                casteList.add(res.getCasteData().get(i).getCasteName());
            }

            if (casteList.size() > 0)
            {
                if (isFirstTimeCaste)
                {
                    binding.etCaste.setTextColor(getResources().getColor(R.color.grey));

                    if (caste != null)
                    {
                        binding.etCaste.setText(caste);
                    }
                    else
                    {
                        binding.etCaste.setText(res.getCasteData().get(0).getCasteName());
                    }
                    for (int i=0;i<casteDataList.size();i++)
                    {
                        if (binding.etCaste.getText().toString().toLowerCase().trim().equalsIgnoreCase(casteDataList.get(i).getCasteName().toLowerCase().trim()))
                        {
                            casteId = casteDataList.get(i).getCasteId();
                            binding.etCategory.setText(casteDataList.get(i).getCategoryName());
                        }
                    }

                    if (isEdit)
                    {
                        isFirstTimeCaste = false;
                        binding.etCaste.setTextColor(getResources().getColor(R.color.white));
                        binding.etCaste.setEnabled(true);
                    }
                    else {
                        binding.etCaste.setTextColor(getResources().getColor(R.color.grey));
                        binding.etCaste.setEnabled(false);
                    }
                }
                else
                {
                    casteId = res.getCasteData().get(0).getCasteId();
                    binding.etCaste.setText(res.getCasteData().get(0).getCasteName());
                    binding.etCategory.setText(res.getCasteData().get(0).getCategoryName());
                }

                searchCastFragmentDialog.setData(res.getCasteData());

            }

            if (casteId != null)
            {
                showLoadingBar(binding.progressBar);
          //      binding.progressBar.setVisibility(View.VISIBLE);
                manager.getSubCaste(VoterProfileActivity.this,casteId);
            }



        }

        else if (LeafManager.API_SUB_CASTE_GET == apiId)
        {
            SubCasteResponse res = (SubCasteResponse) response;

            AppLog.e(TAG, "SubCasteResponse" + res);

            casteId = null;

            subCasteList.clear();

            for (int i=0;i<res.getSubCasteData().size();i++)
            {
                subCasteList.add(res.getSubCasteData().get(i).getSubCasteName());
            }


            if (subCasteList.size() > 0)
            {
                if (isFirstTimeSubCaste)
                {
                    binding.etSubCaste.setTextColor(getResources().getColor(R.color.grey));

                    if (subcaste != null)
                    {
                        binding.etSubCaste.setText(subcaste);
                    }
                    else
                    {
                        binding.etSubCaste.setText(res.getSubCasteData().get(0).getSubCasteName());
                    }


                    if (isEdit)
                    {
                        isFirstTimeSubCaste = false;
                        binding.etSubCaste.setTextColor(getResources().getColor(R.color.white));
                        binding.etSubCaste.setEnabled(true);
                    }
                    else {
                        binding.etSubCaste.setTextColor(getResources().getColor(R.color.grey));
                        binding.etSubCaste.setEnabled(false);
                    }
                }
                else
                {
                    binding.etSubCaste.setText(res.getSubCasteData().get(0).getSubCasteName());
                }

                searchSubCasteDialogFragment.setData(res.getSubCasteData());
            }
        }

        if (apiId == LeafManager.API_VOTER_PROFILE_UPDATE)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_profile_update), Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            setResult(RESULT_OK,i);
            finish();
        }
    }

    private void fillDetails(VoterProfileResponse.VoterData data) {


        genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_disable, R.id.tvItem, genderArray);
        binding.etGender.setAdapter(genderAdapter);
        binding.etGender.setEnabled(false);

        bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_disable, R.id.tvItem, bloodGrpArray);
        binding.etBlood.setAdapter(bloodGrpAdapter);
        binding.etBlood.setEnabled(false);

        binding.etName.setEnabled(false);
        binding.etName.setTextColor(getResources().getColor(R.color.grey));

        binding.etRole.setEnabled(false);
        binding.etRole.setTextColor(getResources().getColor(R.color.grey));

        binding.etPhone.setEnabled(false);
        binding.etPhone.setTextColor(getResources().getColor(R.color.grey));

        binding.etAddress.setEnabled(false);
        binding.etAddress.setTextColor(getResources().getColor(R.color.grey));

        binding.etVoterId.setEnabled(false);
        binding.etVoterId.setTextColor(getResources().getColor(R.color.grey));

        binding.etEmail.setEnabled(false);
        binding.etEmail.setTextColor(getResources().getColor(R.color.grey));

        binding.etdob.setEnabled(false);
        binding.etdob.setTextColor(getResources().getColor(R.color.grey));

        binding.etEducation.setEnabled(false);
        binding.etEducation.setTextColor(getResources().getColor(R.color.grey));

        binding.etProfession.setEnabled(false);
        binding.etProfession.setTextColor(getResources().getColor(R.color.grey));

        binding.etCategory.setTextColor(getResources().getColor(R.color.grey));

   /*     binding.etCaste.setEnabled(false);
        binding.etCaste.setTextColor(getResources().getColor(R.color.grey));

        binding.etSubCaste.setEnabled(false);
        binding.etSubCaste.setTextColor(getResources().getColor(R.color.grey));

        binding.etReligion.setEnabled(false);
        binding.etReligion.setTextColor(getResources().getColor(R.color.grey));*/

        religion = data.religion;
        caste = data.caste;
        subcaste = data.subcaste;


        binding.etName.setText(data.name);
        binding.etRole.setText(data.roleOnConstituency);
        binding.etPhone.setText(data.phone);
        binding.etAddress.setText(data.address);
        binding.etVoterId.setText(data.voterId);
        binding.etEmail.setText(data.email);
        binding.etdob.setText(data.dob);
        binding.etEducation.setText(data.qualification);
        binding.etProfession.setText(data.designation);
       /* binding.etCaste.setText(data.caste);
        binding.etSubCaste.setText(data.subcaste);
        binding.etReligion.setText(data.religion);*/


       int index = 0;

       if (data.gender != null)
       {
           for (int i = 0; i < genderArray.length; i++) {
               if ((data.gender + "").equals(genderArray[i])) {
                   index = i;
               }
           }
       }
       indexGender = index;
       binding.etGender.setSelection(index);

        int index1 = 0;

        if (data.bloodGroup != null)
        {
            for (int i = 0; i < bloodGrpArray.length; i++) {
                if ((data.bloodGroup + "").equals(bloodGrpArray[i])) {
                    index1 = i;
                }
            }
        }
        indexBlood = index1;
        binding.etBlood.setSelection(index1);

        profileImage = data.image;

        if (data.image != null && !data.image.isEmpty() && Constants.decodeUrlToBase64(data.image).contains("http")) {
            imageFragment.updatePhotoFromUrl(data.image);
        } else if (data.image == null) {
            Log.e("ProfileActivity", "image is Null From API ");
            imageFragment.setInitialLatterImage(data.name);
        }

        showLoadingBar(binding.progressBar);
       // binding.progressBar.setVisibility(View.VISIBLE);
        manager.getReligion(this);

    }
    private boolean isValid() {
        boolean valid = true;

        try {
            if (!isValueValid(binding.etName)) {
                valid = false;
            }

            if (!binding.etPhone.getText().toString().isEmpty()) {

                if (binding.etPhone.getText().toString().length() < 10) {
                    binding.etPhone.setError(getString(R.string.msg_valid_phone));
                    valid = false;
                }
            }

           if (!binding.etEmail.getText().toString().isEmpty())
            {
                if (!isValidEmail(binding.etEmail.getText().toString()))
                {
                    binding.etEmail.setError("Invalid Email");
                    valid = false;
                }
            }
           /* if (!isValueValid(binding.etAddress)) {
                valid = false;
            }
            if (!isValueValid(binding.etVoterId))
            {
                valid = false;
            }*/


           /* else
            {
                binding.etEmail.setError("Required");
                valid = false;
            }*/

        } catch (NullPointerException e) {
            valid = false;
        }

        return valid;
    }


    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        // binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onFailure"+msg);
    }


    public void onException(int apiId, String msg) {
        hideLoadingBar();
        // binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onException"+msg);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageFragment != null)
            imageFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSelected(CasteResponse.CasteData casteData) {

        binding.etCaste.setText(casteData.getCasteName());
        binding.etCategory.setText(casteData.getCategoryName());

        casteId = casteData.getCasteId();

        if (casteId != null)
        {
            showLoadingBar(binding.progressBar);
       //     binding.progressBar.setVisibility(View.VISIBLE);
            manager.getSubCaste(this,casteId);
        }
    }

    @Override
    public void onSelected(SubCasteResponse.SubCasteData casteData) {

        binding.etSubCaste.setText(casteData.getSubCasteName());

    }
}