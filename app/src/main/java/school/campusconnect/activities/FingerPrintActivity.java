package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.databinding.ActivityFingerPrintBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class FingerPrintActivity extends BaseActivity {
ActivityFingerPrintBinding binding;
LeafManager manager;
public static final String TAG = "FingerPrintActivity";
private String groupId = null , groupCount = null ,Role = null,Token = null,Pin = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_finger_print);

        manager = new LeafManager();


        if (getIntent() != null)
        {
            groupId = getIntent().getStringExtra("groupID");
            groupCount = getIntent().getStringExtra("groupCount");
            Role =  getIntent().getStringExtra("Role");
            Token = getIntent().getStringExtra("token");
            Pin = getIntent().getStringExtra("pin");
        }
        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.FINGERPRINT,false);
                gotoHomeScreen();
            }
        });

        binding.btnAddFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBiometricSupport())
                {
                    BiometricPrompt biometricPrompt = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        biometricPrompt = new BiometricPrompt.Builder(FingerPrintActivity.this)
                                .setTitle("Set FingerPrint")
                                .setDescription("This app uses biometric authentication to protect your data.")
                                .setNegativeButton(getResources().getString(R.string.lbl_cancel), getMainExecutor(),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Log.e(TAG,"CancellationSignal Click");
                                            }
                                        })
                                .build();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        biometricPrompt.authenticate(getCancellationSignal(), getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                                Toast.makeText(getApplicationContext(),errString,Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"onAuthenticationError "+errString);
                            }

                            @Override
                            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                                super.onAuthenticationHelp(helpCode, helpString);
                                Toast.makeText(getApplicationContext(),helpString,Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"onAuthenticationHelp "+helpString);
                            }

                            @Override
                            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                Log.e(TAG,"onAuthenticationSucceeded ");
                                LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.FINGERPRINT,true);
                                gotoHomeScreen();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.error),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }




    private CancellationSignal getCancellationSignal() {
        CancellationSignal cancellationSignal;
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                Log.e(TAG,"CancellationSignal Click");
            }
        });
        return cancellationSignal;
    }




    private boolean checkBiometricSupport() {

        BiometricManager biometricManager = BiometricManager.from(getApplicationContext());
        Log.e(TAG,"code "+biometricManager.canAuthenticate());

        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
        {
            return true;
        }
        else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE)
        {
            Log.e(TAG, "No biometric features available on this device.");
            Toast.makeText(getApplicationContext(),"No biometric features available on this device.",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE)
        {
            Log.e(TAG, "Biometric features are currently unavailable.");
            Toast.makeText(getApplicationContext(),"Biometric features are currently unavailable.",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED)
        {
            Log.e(TAG, "Add FigerPrint.");
            Toast.makeText(getApplicationContext(),"Add FigerPrint.",Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private void gotoHomeScreen() {

        LeafPreference.getInstance(this).setString(LeafPreference.PIN,Pin);
        LeafPreference.getInstance(this).setString(LeafPreference.TOKEN,Token);

        if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {

            Log.e(TAG,"groupId "+groupId);
            Log.e(TAG,"groupCount "+groupCount);

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


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        //  binding.progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_JOIN_GROUP) {
            AppLog.e("UserExist->", "join group api response");
            showLoadingBar(binding.progressBar,false);
            // binding.progressBar.setVisibility(View.VISIBLE);

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
        hideLoadingBar();
        //binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_JOIN_GROUP) {
            AppLog.e("UserExist-> on Failure", "join group api response");

            AppLog.e("UserExist->", "getGroupDetail api called");
            manager.getGroupDetail(this, BuildConfig.APP_ID);
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //binding.progressBar.setVisibility(View.GONE);
    }
}