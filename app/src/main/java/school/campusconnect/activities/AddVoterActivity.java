package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityAddVoterBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.masterList.VoterListModelResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.UploadImageFragment;

public class AddVoterActivity extends BaseActivity implements View.OnClickListener,LeafManager.OnCommunicationListener{


    private static final String TAG = "AddVoterActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    ActivityAddVoterBinding binding;

    private int currentCountry;

    private boolean isEdit=false;

    private UploadImageFragment imageFragment;

    private LeafManager manager;

    private String team_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_voter);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_add_voter));


        inits();

        onClickListener();

       /* currentCountry = 1;
        String[] str = getResources().getStringArray(R.array.array_country);
        binding.etCountry.setText(str[0]);*/

        setImageFragment();

    }

    private void onClickListener() {

        binding.etdob.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    private void setImageFragment() {
        if (isEdit) {
          /*  imageFragment = UploadImageFragment.newInstance(null, true, true);
            binding.btnAdd.setText("Update");
            setTitle("Student Detail - ("+getIntent().getStringExtra("className")+")");*/
        } else {
            imageFragment = UploadImageFragment.newInstance(null, true, true);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void inits() {

        manager = new LeafManager();

        if (getIntent() != null)
        {
            team_id = getIntent().getStringExtra("team_id");
        }

        String[] bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        ArrayAdapter<String> bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        binding.etBlood.setAdapter(bloodGrpAdapter);

        String[] genderArray = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
        binding.etGender.setAdapter(genderAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.etdob:
                DatePickerFragment fragment = DatePickerFragment.newInstance();
                fragment.setOnDateSelectListener(new DatePickerFragment.OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Calendar c) {
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        binding.etdob.setText(format.format(c.getTime()));
                    }
                });
                fragment.show(getSupportFragmentManager(), "datepicker");
                break;

            case R.id.btnAdd:

                if (isValid())
                {
                    if (isEdit)
                    {

                    }
                    else
                    {
                        VoterListModelResponse.AddVoterReq req = new VoterListModelResponse.AddVoterReq();
                        req.name = binding.etName.getText().toString();
                        req.countryCode = "IN";
                        req.phone = binding.etPhone.getText().toString();
                        req.image = imageFragment.getmProfileImage();
                        req.husbandName = binding.etHusbandName.getText().toString();
                        req.fatherName = binding.etfatherName.getText().toString();
                        req.voterId = binding.etVoterId.getText().toString();
                        req.serialNumber = binding.etSerialNumber.getText().toString();
                        req.address = binding.etAddress.getText().toString();
                        req.dob = binding.etdob.getText().toString();
                        req.age = binding.etAge.getText().toString();
                        req.gender = binding.etGender.getSelectedItem().toString();
                        req.address = binding.etAadhar.getText().toString();
                        req.bloodGroup = binding.etBlood.getSelectedItem().toString();
                        req.email = binding.etEmail.getText().toString();


                        AppLog.e(TAG, "send data : " + new Gson().toJson(req));
                        binding.progressBar.setVisibility(View.VISIBLE);
                        manager.addVoterMasterList(this, GroupDashboardActivityNew.groupId, team_id, req);

                    }
                }

        }
    }

    private boolean isValid() {

        if (!isValueValid(binding.etName)) {
           // Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValueValid(binding.etVoterId)) {
           // Toast.makeText(this, "Enter Voter ID", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!binding.etEmail.getText().toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText()).matches()) {
            Toast.makeText(this, "Enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.ADD_VOTER_MASTER_LIST)
        {
            Toast.makeText(getApplicationContext(),"Voter Added SuccessFully...",Toast.LENGTH_SHORT).show();
            finish();
        }
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
}