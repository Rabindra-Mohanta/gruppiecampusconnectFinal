package school.campusconnect.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.api.client.json.Json;
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
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ProfileItemUpdate;
import school.campusconnect.datamodel.booths.BoothVotersListResponse;
import school.campusconnect.datamodel.booths.VoterProfileResponse;
import school.campusconnect.datamodel.booths.VoterProfileUpdate;
import school.campusconnect.datamodel.masterList.VoterListModelResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.UploadCircleImageFragment;

public class VoterProfileActivity extends BaseActivity implements LeafManager.OnCommunicationListener{

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

    LeafManager manager;

    String userID;

    int indexGender,indexBlood;
    public static String profileImage;

    boolean isEdit = false;

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
            binding.progressBar.setVisibility(View.VISIBLE);
            manager.getVoterProfile(this,GroupDashboardActivityNew.groupId,userID);
        }
        else
        {
            showNoNetworkMsg();
        }
    }

    private void inits() {



        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("name"));

        if (getIntent() != null)
        {
            userID = getIntent().getStringExtra("userID");
        }

        manager = new LeafManager();



        bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        genderArray = getResources().getStringArray(R.array.gender_array);


        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();



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

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isEdit) {
                    isEdit = true;


                    binding.etName.setEnabled(true);
                    binding.etName.setTextColor(getResources().getColor(R.color.white));

                    binding.etPhone.setEnabled(true);
                    binding.etPhone.setTextColor(getResources().getColor(R.color.white));

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

                    binding.etCaste.setEnabled(true);
                    binding.etCaste.setTextColor(getResources().getColor(R.color.white));

                    binding.etSubCaste.setEnabled(true);
                    binding.etSubCaste.setTextColor(getResources().getColor(R.color.white));

                    binding.etReligion.setEnabled(true);
                    binding.etReligion.setTextColor(getResources().getColor(R.color.white));

                    genderAdapter = new ArrayAdapter<String>(VoterProfileActivity.this, R.layout.item_spinner, R.id.tvItem, genderArray);
                    binding.etGender.setAdapter(genderAdapter);
                    binding.etGender.setEnabled(true);

                    bloodGrpAdapter = new ArrayAdapter<String>(VoterProfileActivity.this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
                    binding.etBlood.setAdapter(bloodGrpAdapter);
                    binding.etBlood.setEnabled(true);

                    binding.etGender.setSelection(indexGender);
                    binding.etBlood.setSelection(indexBlood);

                    binding.btnAdd.setText("Save");
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
        item.qualification = binding.etEducation.getText().toString();
        item.designation = binding.etProfession.getText().toString();

        item.caste = binding.etCaste.getText().toString();
        item.subcaste = binding.etSubCaste.getText().toString();
        item.religion = binding.etReligion.getText().toString();


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
        binding.progressBar.setVisibility(View.VISIBLE);
        manager.updateProfileVoter(VoterProfileActivity.this,GroupDashboardActivityNew.groupId,userID, item);

    }


    public void onSuccess(int apiId, BaseResponse response) {
        binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_VOTER_PROFILE_GET)
        {
            VoterProfileResponse res = (VoterProfileResponse) response;

            fillDetails(res.getData());

        }
        if (apiId == LeafManager.API_VOTER_PROFILE_UPDATE)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_profile_update), Toast.LENGTH_LONG).show();
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

        binding.etCaste.setEnabled(false);
        binding.etCaste.setTextColor(getResources().getColor(R.color.grey));

        binding.etSubCaste.setEnabled(false);
        binding.etSubCaste.setTextColor(getResources().getColor(R.color.grey));

        binding.etReligion.setEnabled(false);
        binding.etReligion.setTextColor(getResources().getColor(R.color.grey));


        binding.etName.setText(data.name);
        binding.etPhone.setText(data.phone);
        binding.etAddress.setText(data.address);
        binding.etVoterId.setText(data.voterId);
        binding.etEmail.setText(data.email);
        binding.etdob.setText(data.dob);
        binding.etEducation.setText(data.qualification);
        binding.etProfession.setText(data.designation);
        binding.etCaste.setText(data.caste);
        binding.etSubCaste.setText(data.subcaste);
        binding.etReligion.setText(data.religion);


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

    }
    private boolean isValid() {
        boolean valid = true;
        try {
            if (!isValueValid(binding.etName)) {
                valid = false;
            }
            if (!isValueValid(binding.etAddress)) {
                valid = false;
            }
            if (!isValueValid(binding.etVoterId))
            {
                valid = false;
            }

            if (isValueValid(binding.etEmail))
            {
                if (!isValidEmail(binding.etEmail.getText().toString()))
                {
                    binding.etEmail.setError("Invalid Email");
                    valid = false;
                }
            }
            else
            {
                binding.etEmail.setError("Required");
                valid = false;
            }

        } catch (NullPointerException e) {
            valid = false;
        }

        return valid;
    }


    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onFailure"+msg);
    }


    public void onException(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onException"+msg);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageFragment != null)
            imageFragment.onActivityResult(requestCode, resultCode, data);
    }
}