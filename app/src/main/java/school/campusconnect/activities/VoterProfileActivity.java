package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.api.client.json.Json;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityVoterProfileBinding;
import school.campusconnect.datamodel.booths.BoothVotersListResponse;
import school.campusconnect.fragments.DatePickerFragment;
import school.campusconnect.utils.UploadCircleImageFragment;

public class VoterProfileActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    ActivityVoterProfileBinding binding;
    BoothVotersListResponse.VoterData data;

    UploadCircleImageFragment imageFragment;

    String[] bloodGrpArray;
    String[] genderArray;
    ArrayAdapter<String> genderAdapter;
    ArrayAdapter<String> bloodGrpAdapter;

    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_voter_profile);

        inits();
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

       // data =  (BoothVotersListResponse.VoterData) bundle.getSerializable("data");

        bloodGrpArray = getResources().getStringArray(R.array.blood_group);
        genderArray = getResources().getStringArray(R.array.gender_array);

        genderAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, genderArray);
        binding.etGender.setAdapter(genderAdapter);

        bloodGrpAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, R.id.tvItem, bloodGrpArray);
        binding.etBlood.setAdapter(bloodGrpAdapter);

        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();

      /*  binding.etName.setText(data.name);
        binding.etPhone.setText(data.phone);
        binding.etdob.setText(data.dob);
        binding.etVoterId.setText(data.voterId);*/

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

    }
}