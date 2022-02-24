package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityLoginPinBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class LoginPinActivity extends BaseActivity implements LeafManager.OnCommunicationListener{
    public static String TAG = "LoginPinActivity";
    ActivityLoginPinBinding binding;
    private LeafManager manager;
    private String groupId = null , groupCount = null ,Role = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login_pin);

        inits();
    }


    private void inits() {

        manager = new LeafManager();

        if (LeafPreference.getInstance(this).getString(LeafPreference.PIN).isEmpty())
        {
            if (getIntent()!= null && getIntent().getStringExtra("set_otp") != null)
            {
                groupId = getIntent().getStringExtra("groupID");
                groupCount = getIntent().getStringExtra("groupCount");
                Role =  getIntent().getStringExtra("Role");

                binding.lblForgot.setVisibility(View.GONE);
                binding.lblHint.setText("Confirm Pin");
                binding.btnNext.setText("Confirm");
            }
            else
            {
                groupId = getIntent().getStringExtra("groupID");
                groupCount = getIntent().getStringExtra("groupCount");
                Role =  getIntent().getStringExtra("Role");

                binding.lblForgot.setVisibility(View.GONE);
                binding.lblHint.setText("Set Pin");
                binding.btnNext.setText("Set Pin");
            }
        }
        else
        {
            binding.lblForgot.setVisibility(View.VISIBLE);
            binding.lblHint.setText("Pin");
            binding.btnNext.setText("Next");
        }

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeScreen();
            }
        });
        binding.lblForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void HomeScreen() {

        if (binding.btnNext.getText().toString().equalsIgnoreCase("Next"))
        {
            if (isValid())
            {
               gotoHomeScreenThroughSplash();
            }
        }

        else if (binding.btnNext.getText().toString().equalsIgnoreCase("Confirm"))
        {
            if (isValidConfirm())
            {
                LeafPreference.getInstance(this).setString(LeafPreference.PIN,binding.etPin.getText().toString());
                if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {

                    LeafPreference.getInstance(getApplicationContext()).setInt(LeafPreference.CONST_GROUP_COUNT, Integer.parseInt(groupCount));

                    if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1) {
                        Intent login = new Intent(this, ConstituencyListActivity.class);
                        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(login);
                        finish();
                    } else {
                        manager = new LeafManager();
                        manager.getGroupDetail(this, groupId);
                    }
                } else {
                    if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
                        AppLog.e("UserExist->", "join group api called");
                        LeafPreference.getInstance(getApplicationContext()).setInt(LeafPreference.GROUP_COUNT, Integer.parseInt(groupCount));
                        if ("taluk".equalsIgnoreCase(Role)) {
                            Intent login = new Intent(this, TalukListActivity.class);
                            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                            finish();
                        } else {

                            if (Integer.parseInt(groupCount) > 1) {
                                Intent login = new Intent(this, Home.class);
                                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(login);
                                finish();
                            } else {
                                manager = new LeafManager();
                                manager.getGroupDetail(this, groupId);
                            }
                        }
                    } else {
                        AppLog.e("UserExist->", "join Direct group api called");
                        manager = new LeafManager();
                        manager.joinGroupDirect(this, BuildConfig.APP_ID);
                    }
                }
            }
        }

        else if (binding.btnNext.getText().toString().equalsIgnoreCase("Set Pin"))
        {
            if (isSetValid())
            {
                Intent i = new Intent(getApplicationContext(),LoginPinActivity.class);
                i.putExtra("set_otp",binding.etPin.getText().toString());
                i.putExtra("Role",Role);
                i.putExtra("groupCount",groupCount);
                i.putExtra("groupID",groupId);
                startActivity(i);
            }
        }

    }
    private boolean isValidConfirm() {

        if (binding.etPin.getText().toString().isEmpty() || binding.etPin.getText().toString().length() < 4)
        {
            showToast("Enter Confirm Pin");
            return false;
        }
        if (!binding.etPin.getText().toString().equalsIgnoreCase(getIntent().getStringExtra("set_otp")))
        {
            showToast("Confirm Pin Wrong");
            return false;
        }
        return true;
    }
    private boolean isSetValid() {
        if (binding.etPin.getText().toString().isEmpty() || binding.etPin.getText().toString().length() < 4)
        {
            showToast("Enter Pin");
            return false;
        }
        return true;
    }
    private boolean isValid() {

        if (binding.etPin.getText().toString().isEmpty() || binding.etPin.getText().toString().length() < 4)
        {
            showToast("Enter Pin");
            return false;
        }
        else if (!binding.etPin.getText().toString().equalsIgnoreCase(LeafPreference.getInstance(this).getString(LeafPreference.PIN)))
        {
            showToast("Enter Valid Pin");
            return false;
        }
        return true;
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        binding.progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_JOIN_GROUP) {
            AppLog.e("UserExist->", "join group api response");
            binding.progressBar.setVisibility(View.VISIBLE);

            AppLog.e("UserExist->", "getGroupDetail api called");
            manager.getGroupDetail(this, BuildConfig.APP_ID);
        }
        if (apiId == LeafManager.API_ID_GROUP_DETAIL) {
            AppLog.e("UserExist->", "getGroupDetail api response");

            GroupDetailResponse gRes = (GroupDetailResponse) response;
            AppLog.e(TAG, "group detail ->" + new Gson().toJson(gRes));

            LeafPreference.getInstance(this).setInt(Constants.TOTAL_MEMBER, gRes.data.get(0).totalUsers);

            //save group detail
            LeafPreference.getInstance(this).setString(Constants.GROUP_DATA, new Gson().toJson(gRes.data.get(0)));

            FirebaseMessaging.getInstance().subscribeToTopic(gRes.data.get(0).getGroupId())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AppLog.e(TAG, "subscribeToTopic : Successful()");
                            } else {
                                AppLog.e(TAG, "subscribeToTopic : Fail()");
                            }

                        }
                    });

            Intent login = new Intent(this, GroupDashboardActivityNew.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            finish();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_JOIN_GROUP) {
            AppLog.e("UserExist-> on Failure", "join group api response");

            AppLog.e("UserExist->", "getGroupDetail api called");
            manager.getGroupDetail(this, BuildConfig.APP_ID);
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void gotoHomeScreenThroughSplash() {

        if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
            if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1) {
                Intent login = new Intent(this, ConstituencyListActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
            } else {
                Intent login = new Intent(this, GroupDashboardActivityNew.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(login);
                finish();
            }

        } else {
            if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
                if ("taluk".equalsIgnoreCase(LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.ROLE))) {
                    Intent login = new Intent(this, TalukListActivity.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                    finish();
                } else {
                    if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.GROUP_COUNT) > 1) {
                        Intent intent = new Intent(LoginPinActivity.this, Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent login = new Intent(this, GroupDashboardActivityNew.class);
                        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(login);
                        finish();
                    }
                }

            } else {
                Intent intent = new Intent(LoginPinActivity.this, GroupDashboardActivityNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }
    }
}