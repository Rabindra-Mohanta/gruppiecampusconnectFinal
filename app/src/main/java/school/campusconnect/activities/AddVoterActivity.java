package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
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
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityAddVoterBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.masterList.VoterListModelResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.UploadCircleImageFragment;
import school.campusconnect.utils.UploadImageFragment;

public class AddVoterActivity extends BaseActivity implements View.OnClickListener,LeafManager.OnCommunicationListener{


    private static final String TAG = "AddVoterActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    ActivityAddVoterBinding binding;

    private int currentCountry;

    private boolean isEdit=false;
    private boolean isEditData = false;

    UploadCircleImageFragment imageFragment;
    String[] bloodGrpArray;
    String[] genderArray;

    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> bloodGrpAdapter;

    private LeafManager manager;
    VoterListModelResponse.VoterData voterData;
    private String team_id;

    int indexGender,indexBlood;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_voter);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        inits();
        onClickListener();

    }

    private void onClickListener() {

        binding.etdob.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_delete_issue,menu);

        if (isEdit)
        {
            menu.findItem(R.id.menuDelete).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.menuDelete).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent() != null)
        {
            if (getIntent().getBooleanExtra("edit",false))
            {
                isEdit = true;
                voterData = (VoterListModelResponse.VoterData) getIntent().getSerializableExtra("data");
                team_id = voterData.teamId;
                Log.e(TAG,"voterData" +new Gson().toJson(voterData));


                setTitle(voterData.name);

                setData(voterData);

            }
            else
            {

                setTitle(getResources().getString(R.string.title_add_voter));

                team_id = getIntent().getStringExtra("team_id");
                bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
                binding.etBlood.setAdapter(bloodGrpAdapter);

                genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
                binding.etGender.setAdapter(genderAdapter);

                imageFragment.setEditEnabled(true);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menuDelete:
                AppDialog.showConfirmDialog(this, getResources().getString(R.string.dialog_are_you_want_to_delete), new AppDialog.AppDialogListener() {
                    @Override
                    public void okPositiveClick(DialogInterface dialog) {
                        callDeleteApi();
                    }

                    @Override
                    public void okCancelClick(DialogInterface dialog) {

                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callDeleteApi() {
        showLoadingBar(binding.progressBar);
        manager.deleteVoterMaster(this, GroupDashboardActivityNew.groupId, team_id, voterData.voterId);
    }

    private void inits() {

        manager = new LeafManager();

        bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        genderArray = getResources().getStringArray(R.array.gender_array);

        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();

    }

    private void setData(VoterListModelResponse.VoterData voterData) {

        binding.etName.setEnabled(false);
        binding.etName.setTextColor(getResources().getColor(R.color.grey));
        binding.etName.setText(voterData.name);

        binding.etPhone.setEnabled(false);
        binding.etPhone.setTextColor(getResources().getColor(R.color.grey));
        binding.etPhone.setText(voterData.phone);

        binding.etfatherName.setEnabled(false);
        binding.etfatherName.setTextColor(getResources().getColor(R.color.grey));
        binding.etfatherName.setText(voterData.fatherName);

        binding.etHusbandName.setEnabled(false);
        binding.etHusbandName.setTextColor(getResources().getColor(R.color.grey));
        binding.etHusbandName.setText(voterData.husbandName);

        binding.etVoterId.setEnabled(false);
        binding.etVoterId.setTextColor(getResources().getColor(R.color.grey));
        binding.etVoterId.setText(voterData.voterId);

        binding.etSerialNumber.setEnabled(false);
        binding.etSerialNumber.setTextColor(getResources().getColor(R.color.grey));
        binding.etSerialNumber.setText(voterData.serialNumber);

        binding.etAddress.setEnabled(false);
        binding.etAddress.setTextColor(getResources().getColor(R.color.grey));
        binding.etAddress.setText(voterData.address);

        binding.etdob.setEnabled(false);
        binding.etdob.setTextColor(getResources().getColor(R.color.grey));
        binding.etdob.setText(voterData.dob);

        binding.etAge.setEnabled(false);
        binding.etAge.setTextColor(getResources().getColor(R.color.grey));
        binding.etAge.setText(voterData.age);

        binding.etAadhar.setEnabled(false);
        binding.etAadhar.setTextColor(getResources().getColor(R.color.grey));
        binding.etAadhar.setText(voterData.aadharNumber);

        binding.etEmail.setEnabled(false);
        binding.etEmail.setTextColor(getResources().getColor(R.color.grey));
        binding.etEmail.setText(voterData.email);

        genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_disable, R.id.tvItem, genderArray);
        binding.etGender.setAdapter(genderAdapter);
        binding.etGender.setEnabled(false);

        bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_disable, R.id.tvItem, bloodGrpArray);
        binding.etBlood.setAdapter(bloodGrpAdapter);
        binding.etBlood.setEnabled(false);

        int index = 0;
        for (int i = 0; i < genderArray.length; i++) {
            if ((voterData.gender + "").equals(genderArray[i])) {
                index = i;
            }
        }
        indexGender = index;
        binding.etGender.setSelection(index);

        int index1 = 0;
        for (int i = 0; i < bloodGrpArray.length; i++) {
            if ((voterData.bloodGroup + "").equals(bloodGrpArray[i])) {
                index1 = i;
            }
        }
        indexBlood = index1;
        binding.etBlood.setSelection(index1);

        if (voterData.image != null && !voterData.image.isEmpty() && Constants.decodeUrlToBase64(voterData.image).contains("http"))
        {
            imageFragment.updatePhotoFromUrl(voterData.image);
        }
        else if (voterData.image == null)
        {
            Log.e("ProfileActivity", "image is Null From API "+voterData.name);
            imageFragment.setInitialLatterImage(voterData.name);
        }
        binding.btnAdd.setText(getResources().getString(R.string.lbl_edit));

        imageFragment.setEditEnabled(false);
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

                if (isEdit)
                {
                    if (!isEditData)
                    {
                        isEditData = true;

                        genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
                        binding.etGender.setAdapter(genderAdapter);
                        binding.etGender.setEnabled(true);

                        bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
                        binding.etBlood.setAdapter(bloodGrpAdapter);
                        binding.etBlood.setEnabled(true);

                        binding.etGender.setSelection(indexGender);
                        binding.etBlood.setSelection(indexBlood);

                        if(imageFragment !=null)
                        {
                            imageFragment.setEditEnabled(true);
                        }

                        binding.etName.setEnabled(true);
                        binding.etName.setTextColor(getResources().getColor(R.color.white));
                        binding.etPhone.setEnabled(true);
                        binding.etPhone.setTextColor(getResources().getColor(R.color.white));
                        binding.etfatherName.setEnabled(true);
                        binding.etfatherName.setTextColor(getResources().getColor(R.color.white));
                        binding.etHusbandName.setEnabled(true);
                        binding.etHusbandName.setTextColor(getResources().getColor(R.color.white));
                        binding.etVoterId.setEnabled(true);
                        binding.etVoterId.setTextColor(getResources().getColor(R.color.white));
                        binding.etSerialNumber.setEnabled(true);
                        binding.etSerialNumber.setTextColor(getResources().getColor(R.color.white));
                        binding.etAddress.setEnabled(true);
                        binding.etAddress.setTextColor(getResources().getColor(R.color.white));
                        binding.etdob.setEnabled(true);
                        binding.etdob.setTextColor(getResources().getColor(R.color.white));
                        binding.etAge.setEnabled(true);
                        binding.etAge.setTextColor(getResources().getColor(R.color.white));
                        binding.etAadhar.setEnabled(true);
                        binding.etAadhar.setTextColor(getResources().getColor(R.color.white));
                        binding.etEmail.setEnabled(true);
                        binding.etEmail.setTextColor(getResources().getColor(R.color.white));

                        binding.btnAdd.setText(getResources().getString(R.string.lbl_save));
                        return;
                    }
                    else
                    {
                        if (isValid())
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
                            req.aadharNumber = binding.etAadhar.getText().toString();
                            req.bloodGroup = binding.etBlood.getSelectedItem().toString();
                            req.email = binding.etEmail.getText().toString();



                            AppLog.e(TAG, "send data : " + new Gson().toJson(req));
                         /*   showLoadingBar(binding.progressBar);
                            // binding.progressBar.setVisibility(View.VISIBLE);
                            manager.addVoterMasterList(this, GroupDashboardActivityNew.groupId, team_id, req);*/
                        }
                    }

                }
                else
                {
                    if (isValid())
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
                        req.aadharNumber = binding.etAadhar.getText().toString();
                        req.bloodGroup = binding.etBlood.getSelectedItem().toString();
                        req.email = binding.etEmail.getText().toString();


                        AppLog.e(TAG, "send data : " + new Gson().toJson(req));
                        showLoadingBar(binding.progressBar);
                        // binding.progressBar.setVisibility(View.VISIBLE);
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
            Toast.makeText(this, getResources().getString(R.string.toast_enter_valid_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
       // binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.ADD_VOTER_MASTER_LIST)
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_voter_add_successfully),Toast.LENGTH_SHORT).show();
            LeafPreference.getInstance(this).setBoolean("VoterUpate",true);
            finish();
        }
        else if (apiId == LeafManager.API_VOTER_MASTER_DELETE)
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_voter_delete_successfully),Toast.LENGTH_SHORT).show();
            LeafPreference.getInstance(this).setBoolean("VoterUpate",true);
            finish();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
      //  binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onFailure"+msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        //  binding.progressBar.setVisibility(View.GONE);
        Log.e(TAG,"onException"+msg);
    }
}