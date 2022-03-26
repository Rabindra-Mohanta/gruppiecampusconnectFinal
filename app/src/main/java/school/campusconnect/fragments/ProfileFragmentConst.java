package school.campusconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.ProfileActivity2;
import school.campusconnect.activities.ProfileConstituencyActivity;
import school.campusconnect.activities.SubjectActivity2;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddressItem;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.ProfileItem;
import school.campusconnect.datamodel.ProfileItemUpdate;
import school.campusconnect.datamodel.ProfileResponse;
import school.campusconnect.datamodel.ProfileValidationError;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.family.FamilyMemberResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AmazoneRemove;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.utils.UploadCircleImageFragment;

public class ProfileFragmentConst extends BaseFragment implements LeafManager.OnCommunicationListener, LeafManager.OnAddUpdateListener<ProfileValidationError> {
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


    @Bind(R.id.etCaste)
    public EditText etCaste;

    @Bind(R.id.etSubCaste)
    public EditText etSubCaste;

    @Bind(R.id.etReligion)
    public EditText etReligion;

    @Bind(R.id.etAadhar)
    public EditText etAadhar;

    @Bind(R.id.btnAdd)
    public Button btnAdd;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    LeafManager leafManager;


    String[] bloodGrpArray;
    String[] genderArray;
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> bloodGrpAdapter;

    UploadCircleImageFragment imageFragment;


    public ProfileItem item;

    public static String profileImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_const, container, false);
        ButterKnife.bind(this, view);
        init();

        progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getProfileDetails(this);

        return view;
    }

    private void init() {
        profileImage = "";
        leafManager = new LeafManager();

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
            item.religion = etReligion.getText().toString();
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


            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_IMAGE, imageFragment.getmProfileImage());
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_NAME, etName.getText().toString());
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_VOTERID, etVoterId.getText().toString());


            if (imageFragment.isImageChanged && TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                manager.deleteProPic(this);
                manager.updateProfileDetails(this, item);
                AppLog.e("Profile Activity", "image deleted " + new Gson().toJson(item));
            } else {
                if (imageFragment.isImageChanged && !TextUtils.isEmpty(imageFragment.getmProfileImage())) {
                    AppLog.e("Profile Activity", "Image Changed.." + new Gson().toJson(item));
                    item.image = imageFragment.getmProfileImage();
                }
                LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_IMAGE, item.image);
                AppLog.e("Profile Activity", "Image Not Changed.." + new Gson().toJson(item));
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

            item = res.data;
            LeafPreference.getInstance(getContext()).setString(LeafPreference.NAME, res.data.name);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_COMPLETE, res.data.profileCompletion);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_IMAGE, res.data.image);
            LeafPreference.getInstance(getContext()).setString(LeafPreference.PROFILE_VOTERID, res.data.voterId);

            LeafPreference.getInstance(getContext()).setString(LeafPreference.EMAIL, res.data.email);
            AppLog.e("PROFILE EMAIL", "emails is " + res.data.email);
            AppLog.e("PROFILE IMAGE", "image is " + res.data.image);
            imageFragment.isImageChanged = false;
            fillDetails(item);
        } else if (LeafManager.API_ID_DELETE_PROPIC == apiId) {
            imageFragment.isImageChanged = false;
            LeafPreference.getInstance(getContext()).setBoolean(LeafPreference.ISPROFILEUPDATED, true);
            AmazoneRemove.remove(item.image);
        } else {
            LeafPreference.getInstance(getContext()).setBoolean(LeafPreference.ISPROFILEUPDATED, true);
            Toast.makeText(getContext(), getString(R.string.msg_profile_update), Toast.LENGTH_LONG).show();
            imageFragment.isImageChanged = false;
            getActivity().finish();
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<ProfileValidationError> error) {

    }

    private void fillDetails(ProfileItem item) {
        etName.setText(item.name);
  //      etRelationShip.setText(item.relationship);
        etAddress.setText(item.email);
        etPhone.setText(item.phone);

        etCaste.setText(item.caste);
        etEducation.setText(item.qualification);
        etReligion.setText(item.religion);
        etSubCaste.setText(item.subcaste);
        etProfession.setText(item.designation);
        
        etPhone.setEnabled(false);
    //    etRelationShip.setEnabled(false);

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
            Log.e("ProfileActivity", "image is Null From API ");
            imageFragment.setInitialLatterImage(item.name);
        }
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
}
