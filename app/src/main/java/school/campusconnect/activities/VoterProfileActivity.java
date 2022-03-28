package school.campusconnect.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

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

       // data =  (BoothVotersListResponse.VoterData) bundle.getSerializable("data");

      //  bloodGrpArray = getResources().getStringArray(R.array.blood_group);
     /*   genderArray = getResources().getStringArray(R.array.gender_array);

        genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
        binding.etGender.setAdapter(genderAdapter);*/

 /*       bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        binding.etBlood.setAdapter(bloodGrpAdapter);*/

       /* imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();*/

      /*  binding.etName.setText(data.name);
        binding.etPhone.setText(data.phone);
        binding.etdob.setText(data.dob);
        binding.etVoterId.setText(data.voterId);*/

        /*binding.etdob.setOnClickListener(new View.OnClickListener() {
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
        });*/

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isEdit) {
                    isEdit = true;
                    binding.etName.setEnabled(true);
                    binding.etPhone.setEnabled(true);

                    binding.etName.setTextColor(getResources().getColor(R.color.white));
                    binding.etPhone.setTextColor(getResources().getColor(R.color.white));

                    binding.btnAdd.setText("Save");
                    return;
                }
                isEdit = false;
                binding.etName.setEnabled(false);
                binding.etPhone.setEnabled(false);
                binding.btnAdd.setText("Edit");

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


    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_VOTER_PROFILE_GET)
        {
            VoterProfileResponse res = (VoterProfileResponse) response;

            fillDetails(res.getData());

        }
    }

    private void fillDetails(VoterProfileResponse.VoterData data) {

        binding.etName.setText(data.name);
        binding.etPhone.setText(data.phone);

        binding.etName.setEnabled(false);
        binding.etName.setTextColor(getResources().getColor(R.color.grey));
        binding.etPhone.setEnabled(false);
        binding.etPhone.setTextColor(getResources().getColor(R.color.grey));

       /* int index = 0;
        for (int i = 0; i < genderArray.length; i++) {
            if ((data.get(0).gender + "").equals(genderArray[i])) {
                index = i;
            }
        }
        binding.etGender.setSelection(index);

        profileImage = data.get(0).image;

        if (data.get(0).image != null && !data.get(0).image.isEmpty() && Constants.decodeUrlToBase64(data.get(0).image).contains("http")) {
            imageFragment.updatePhotoFromUrl(data.get(0).image);
        } else if (data.get(0).image == null) {
            Log.e("ProfileActivity", "image is Null From API ");
            imageFragment.setInitialLatterImage(data.get(0).image);
        }*/

    }
    private boolean isValid() {
        boolean valid = true;
        try {
            if (!isValueValid(binding.etName)) {
                valid = false;
            }
            if (!isValueValid(binding.etPhone)) {
                valid = false;
            }
        } catch (NullPointerException e) {
            valid = false;
        }

        return valid;
    }

    @Override
    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onFailure"+msg);
    }

    @Override
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