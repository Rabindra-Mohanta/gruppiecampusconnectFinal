package school.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.activeandroid.query.From;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.ProfileConstituencyActivity;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.banner.BannerRes;
import school.campusconnect.datamodel.profile.ProfileItem;
import school.campusconnect.datamodel.profile.ProfileItemUpdate;
import school.campusconnect.datamodel.profile.ProfileResponse;
import school.campusconnect.datamodel.ProfileValidationError;
import school.campusconnect.datamodel.profile.ProfileTBL;
import school.campusconnect.datamodel.profileCaste.CasteResponse;
import school.campusconnect.datamodel.profileCaste.ReligionResponse;
import school.campusconnect.datamodel.profileCaste.SubCasteResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.utils.UploadCircleImageFragment;

public class ProfileFragmentConst extends BaseFragment implements LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<ProfileValidationError> ,SearchCastFragmentDialog.SelectListener,SearchSubCasteDialogFragment.SelectListener{
    private static final String TAG = "ProfileFragmentConst";

    @Bind(R.id.etName)
    public EditText etName;

    @Bind(R.id.etPhone)
    public EditText etPhone;

   /* @Bind(R.id.etRelationShip)
    public EditText etRelationShip;*/
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

    @Bind(R.id.etAadhar)
    public EditText etAadhar;

    @Bind(R.id.btnAdd)
    public Button btnAdd;

   /* @Bind(R.id.btnSearchCaste)
    public FrameLayout btnSearchCaste;*/


  /*  @Bind(R.id.btnSearchSubCaste)
    public FrameLayout btnSearchSubCaste;*/


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

    boolean isFirstTimeCaste = true;
    boolean isFirstTimeSubCaste = true;

    String casteId = null;

    String religion = null;
    String caste = null;
    String subcaste = null;

    UploadCircleImageFragment imageFragment;

    SearchCastFragmentDialog searchCastFragmentDialog;
    SearchSubCasteDialogFragment searchSubCasteDialogFragment;

    public ProfileItem item = new ProfileItem();

    public static String profileImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_const, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {

        profileImage = "";
        leafManager = new LeafManager();

        searchCastFragmentDialog = SearchCastFragmentDialog.newInstance();
        searchCastFragmentDialog.setListener(this);

        searchSubCasteDialogFragment = SearchSubCasteDialogFragment.newInstance();
        searchSubCasteDialogFragment.setListener(this);

        bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        genderArray = getResources().getStringArray(R.array.gender_array);

        genderAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner, R.id.tvItem, genderArray);
        etGender.setAdapter(genderAdapter);

        bloodGrpAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        etBlood.setAdapter(bloodGrpAdapter);

        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getChildFragmentManager().executePendingTransactions();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callUpdateProfileApi();
            }
        });

        etCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCastFragmentDialog.show(getFragmentManager(),"");
            }
        });

        etSubCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchSubCasteDialogFragment.show(getFragmentManager(),"");
            }
        });



        etdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etdob.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getChildFragmentManager(), "datepicker");
            }
        });

        etReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.getCaste(ProfileFragmentConst.this,etReligion.getSelectedItem().toString());
                }
                else
                {
                    etCaste.setText("");
                    etSubCaste.setText("");
                    etCategory.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<ProfileTBL> profileTBLList = ProfileTBL.getProfile();

        if (profileTBLList.size() > 0) {
            if (MixOperations.isNewEvent(LeafPreference.getInstance(getContext()).getString("PROFILE_API"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", profileTBLList.get(0)._now)) {
                profileApiCall();
            }
            else
            {
                getDataLocally();
            }
        }
    }

    public void callUpdateProfileApi() {

        if (isValid()) {
            hide_keyboard();

            LeafManager manager = new LeafManager();
            ProfileItemUpdate item = new ProfileItemUpdate();
            item.name = etName.getText().toString();
            item.dob = etdob.getText().toString();
            item.email = etAddress.getText().toString();
            item.address = this.item.address;
            item.occupation = this.item.occupation;

            item.qualification = etEducation.getText().toString();
            item.caste = etCaste.getText().toString();
            item.subcaste = etSubCaste.getText().toString();
            item.religion = etReligion.getSelectedItem().toString();
            item.designation = etProfession.getText().toString();

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

            Log.e(TAG,"profile image "+imageFragment.getmProfileImage());
            Log.e(TAG,"profile name "+ etName.getText().toString());
            Log.e(TAG,"profile voterID "+etVoterId.getText().toString());

            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_IMAGE_NEW, item.image);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_NAME, item.name);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_VOTERID, item.voterId);


            if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                manager.deleteProPic(this);
                manager.updateProfileDetails(this, item);
                AppLog.e("Profile Activity", "image deleted " + new Gson().toJson(item));
            } else {
                if (imageFragment.isImageChanged && !TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                    AppLog.e("Profile Activity", "Image Changed.." + new Gson().toJson(item));
                    item.image = imageFragment.getmProfileImage();
                }
                AppLog.e("Profile Activity", "Image Not Changed.." + new Gson().toJson(item));
                LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_IMAGE_NEW, item.image);
                manager.updateProfileDetails(this, item);
            }

        }
    }

    private boolean isValid() {
        boolean valid = true;
        try {
            if (!isValueValid(etName)) {
                valid = false;
            }
            if (!isValueValid(etPhone)) {
                valid = false;
            } else if (etPhone.getText().toString().length() > 15) {
                etPhone.setError(getString(R.string.msg_valid_phone));
                etPhone.requestFocus();
                valid = false;
            }
        } catch (NullPointerException e) {
            valid = false;
        }

        return valid;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        if (getActivity() == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        if (LeafManager.API_ID_GET_PROFILE == apiId) {
            ProfileResponse res = (ProfileResponse) response;
            AppLog.e(TAG, "ProfileResponse" + res);


            if (res.data != null)
            {
                ProfileTBL.deleteAll();

                ProfileTBL profileTBL = new ProfileTBL();

                profileTBL.profileData = new Gson().toJson(res.data);
                profileTBL._now = System.currentTimeMillis();
                profileTBL.save();
            }

            getDataLocally();



        //    fillDetails(item);

        } else if (LeafManager.API_ID_DELETE_PROPIC == apiId) {
            imageFragment.isImageChanged = false;
            LeafPreference.getInstance(getContext()).setBoolean(LeafPreference.ISPROFILEUPDATED, true);
            AmazoneRemove.remove(item.image);
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
                religionAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_spinner, R.id.tvItem, religionList);
                etReligion.setAdapter(religionAdapter);

            }
            if (religion != null)
            {

                etReligion.setSelection(religionAdapter.getPosition(religion));
            }
            else
            {
                etReligion.setSelection(religionAdapter.getPosition("select religion"));
            }

        }

        else if (LeafManager.API_CASTE_GET == apiId)
        {
            CasteResponse res = (CasteResponse) response;

            AppLog.e(TAG, "CasteResponse" + res);

           /* casteDataList.clear();
            casteDataList.addAll(res.getCasteData());*/

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
                    isFirstTimeCaste = false;

                    if (caste != null)
                    {
                        etCaste.setText(caste);
                    }
                    else
                    {
                        etCaste.setText(res.getCasteData().get(0).getCasteName());
                    }

                    for (int i=0;i<casteDataList.size();i++)
                    {
                        if (etCaste.getText().toString().toLowerCase().trim().equalsIgnoreCase(casteDataList.get(i).getCasteName().toLowerCase().trim()))
                        {
                            casteId = casteDataList.get(i).getCasteId();
                            etCategory.setText(casteDataList.get(i).getCategoryName());
                        }
                    }
                }
                else
                {
                    casteId = res.getCasteData().get(0).getCasteId();
                    etCaste.setText(res.getCasteData().get(0).getCasteName());
                    etCategory.setText(res.getCasteData().get(0).getCategoryName());
                }

                searchCastFragmentDialog.setData(res.getCasteData());

            }

            if (casteId != null)
            {
                progressBar.setVisibility(View.VISIBLE);
                leafManager.getSubCaste(this,casteId);
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
                    isFirstTimeSubCaste = false;

                    if (subcaste != null)
                    {
                        etSubCaste.setText(subcaste);
                    }
                    else
                    {
                        etSubCaste.setText(res.getSubCasteData().get(0).getSubCasteName());
                    }

                }
                else
                {
                    etSubCaste.setText(res.getSubCasteData().get(0).getSubCasteName());
                }

                searchSubCasteDialogFragment.setData(res.getSubCasteData());
            }
        }
        else
            {
            LeafPreference.getInstance(getContext()).setBoolean(LeafPreference.ISPROFILEUPDATED, true);
            Toast.makeText(getContext(), getString(R.string.msg_profile_update), Toast.LENGTH_LONG).show();
            imageFragment.isImageChanged = false;
            getActivity().finish();
        }
    }

    private void getDataLocally() {


        List<ProfileTBL> profileTBLList = ProfileTBL.getProfile();

        if (profileTBLList != null && profileTBLList.size()>0)
        {
            ProfileResponse profileResponse = new ProfileResponse();
            profileResponse.data = new Gson().fromJson(profileTBLList.get(0).profileData,new TypeToken<ProfileItem>() {}.getType());


            LeafPreference.getInstance(getContext()).setString(LeafPreference.NAME, profileResponse.data.name);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_COMPLETE, profileResponse.data.profileCompletion);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_IMAGE, profileResponse.data.image);

            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_IMAGE_NEW, profileResponse.data.image);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_NAME, profileResponse.data.name);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_VOTERID, profileResponse.data.voterId);

            LeafPreference.getInstance(getContext()).setString(LeafPreference.EMAIL, profileResponse.data.email);
            imageFragment.isImageChanged = false;

            item = profileResponse.data;
            fillDetails(profileResponse.data);
        }
        else
        {
            profileApiCall();
        }

    }

    public void profileApiCall()
    {
        progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getProfileDetails(this);
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<ProfileValidationError> error) {

    }


    private void fillDetails(ProfileItem item) {
        etName.setText(item.name);
  //      etRelationShip.setText(item.relationship);
        etAddress.setText(item.email);
        etPhone.setText(item.phone);

        religion = item.religion;
        caste = item.caste;
        subcaste = item.subcaste;

        etEducation.setText(item.qualification);

        etProfession.setText(item.designation);
        etVoterId.setText(item.voterId);
        
        etPhone.setEnabled(false);

        etdob.setText(item.dob);
    //    etRelationShip.setEnabled(false);

        //etSubCaste.setText(item.subcaste);

        int index = 0;
        for (int i = 0; i < genderArray.length; i++) {
            if ((item.gender + "").equals(genderArray[i])) {
                index = i;
            }
        }
        etGender.setSelection(index);

        index = 0;
        for (int i = 0; i < bloodGrpArray.length; i++) {
            if ((item.bloodGroup + "").equals(bloodGrpArray[i])) {
                index = i;
            }
        }
        etBlood.setSelection(index);

        profileImage = item.image;

        if (getActivity() != null)
            ((ProfileConstituencyActivity) getActivity()).setTitle(item.name);

        if (item.image != null && !item.image.isEmpty() && Constants.decodeUrlToBase64(item.image).contains("http")) {
            imageFragment.updatePhotoFromUrl(item.image);
        } else if (item.image == null) {
            Log.e("ProfileActivity", "image is Null From API "+item.name);
            imageFragment.setInitialLatterImage(item.name);
        }

        Log.e(TAG,"profile image "+imageFragment.getmProfileImage());
        Log.e(TAG,"profile name "+ etName.getText().toString());
        Log.e(TAG,"profile voterID "+etVoterId.getText().toString());

        progressBar.setVisibility(View.VISIBLE);
        leafManager.getReligion(this);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageFragment != null)
            imageFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSelected(CasteResponse.CasteData casteData) {

        etCaste.setText(casteData.getCasteName());
        etCategory.setText(casteData.getCategoryName());

        casteId = casteData.getCasteId();

        if (casteId != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            leafManager.getSubCaste(ProfileFragmentConst.this,casteId);
        }
    }

    @Override
    public void onSelected(SubCasteResponse.SubCasteData casteData) {

        etSubCaste.setText(casteData.getSubCasteName());

    }
}
