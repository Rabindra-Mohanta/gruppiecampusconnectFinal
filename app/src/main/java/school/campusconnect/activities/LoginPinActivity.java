package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import in.aabhasjindal.otptextview.OTPListener;
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
    private String ButtonValidation = null;
    private String groupId = null , groupCount = null ,Role = null,Token = null;
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
                Token = getIntent().getStringExtra("token");
                binding.lblForgot.setVisibility(View.GONE);
                binding.btnSkip.setVisibility(View.GONE);
                ButtonValidation = "Confirm";
                binding.lblHint.setText(getResources().getString(R.string.lbl_conPin));
                binding.btnNext.setText(getResources().getString(R.string.done));
            }
            else
            {
                groupId = getIntent().getStringExtra("groupID");
                groupCount = getIntent().getStringExtra("groupCount");
                Role =  getIntent().getStringExtra("Role");
                Token = getIntent().getStringExtra("token");
                binding.lblForgot.setVisibility(View.GONE);
                binding.btnSkip.setVisibility(View.VISIBLE);
                ButtonValidation = "Set Pin";
                binding.lblHint.setText(getResources().getString(R.string.lbl_setPin));
                binding.btnNext.setText(getResources().getString(R.string.next));
            }
        }
        else
        {
            if (LeafPreference.getInstance(this).getBoolean(LeafPreference.FINGERPRINT))
            {
                binding.llFingerPrint.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.llFingerPrint.setVisibility(View.GONE);
            }
            binding.btnSkip.setVisibility(View.GONE);
            binding.lblForgot.setVisibility(View.VISIBLE);
            binding.lblHint.setText(getResources().getString(R.string.txt_pin));
            ButtonValidation = "Next";
            binding.btnNext.setText(getResources().getString(R.string.done));
        }

        Log.e(TAG,"groupId "+groupId);
        Log.e(TAG,"groupCount "+groupCount);


        binding.etPin.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {

                if (otp.length()>0)
                {
                    binding.lblError.setText("");
                }
                /*if (otp.length() == 4)
                {
                    hide_keyboard();
                    HomeScreen();
                }*/
            }
        });

        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipPin();
            }
        });






    /*    binding.etPin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e(TAG,"keyCode "+event.getAction());
                Log.e(TAG,"keyCode "+event.toString());

                if (event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    hide_keyboard();
                    HomeScreen();
                    return true;
                }
                return false;
            }
        });*/



       /* binding.etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length()>0)
                {
                    binding.lblError.setText("");
                }
            }
        });*/

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_keyboard();
                HomeScreen();
            }
        });
        binding.lblForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        binding.btnAddFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBiometricSupport())
                {
                    BiometricPrompt biometricPrompt = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        biometricPrompt = new BiometricPrompt.Builder(LoginPinActivity.this)
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
                                gotoHomeScreenThroughSplash();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.error),Toast.LENGTH_SHORT).show();
                                Log.e(TAG,"onAuthenticationFailed ");
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




    private boolean checkBiometricSupportNewScreen() {

        BiometricManager biometricManager = BiometricManager.from(getApplicationContext());
        Log.e(TAG,"code "+biometricManager.canAuthenticate());

        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
        {
            return true;
        }
        else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE)
        {
            Log.e(TAG, "No biometric features available on this device.");
            return false;
        }
        else if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE)
        {
            Log.e(TAG, "Biometric features are currently unavailable.");
            return false;
        }
        return false;
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

    public boolean checkPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == 0) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }

    }


        private void HomeScreen() {

        if (ButtonValidation.equalsIgnoreCase("Next"))
        {
            if (isValid())
            {
               gotoHomeScreenThroughSplash();
            }
        }
        else if (ButtonValidation.equalsIgnoreCase("Confirm"))
        {
            if (isValidConfirm())
            {

                if (checkBiometricSupportNewScreen())
                {

                    LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.SKIP_PIN,"no");
                    Intent fingerPrint = new Intent(this, FingerPrintActivity.class);
                    fingerPrint.putExtra("Role",Role);
                    fingerPrint.putExtra("groupID",groupId);
                    fingerPrint.putExtra("groupCount",groupCount);
                    fingerPrint.putExtra("pin",binding.etPin.getOTP());
                    fingerPrint.putExtra("token",Token);
                    fingerPrint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(fingerPrint);
                    finish();
                }
                else
                {
                    LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.SKIP_PIN,"no");
                    LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.FINGERPRINT,false);
                    LeafPreference.getInstance(this).setString(LeafPreference.PIN,binding.etPin.getOTP());
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

            }
        }

        else if (ButtonValidation.equalsIgnoreCase("Set Pin"))
        {
            if (isSetValid())
            {
                Intent i = new Intent(getApplicationContext(),LoginPinActivity.class);
                i.putExtra("set_otp",binding.etPin.getOTP());
                i.putExtra("Role",Role);
                i.putExtra("token",Token);
                i.putExtra("groupCount",groupCount);
                i.putExtra("groupID",groupId);
                startActivity(i);
            }
        }

    }
    private void skipPin()
    {
        LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.SKIP_PIN,"yes");
        LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.FINGERPRINT,false);
        LeafPreference.getInstance(this).setString(LeafPreference.PIN,binding.etPin.getOTP());
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
    private boolean isValidConfirm() {

        if (binding.etPin.getOTP().isEmpty() || binding.etPin.getOTP().length() < 4)
        {
            binding.lblError.setText(getResources().getString(R.string.txt_enter_confirm_pin));
            return false;
        }
        if (!binding.etPin.getOTP().equalsIgnoreCase(getIntent().getStringExtra("set_otp")))
        {
            binding.lblError.setText(getResources().getString(R.string.txt_confirm_pin_wrong));
            return false;
        }
        return true;
    }
    private boolean isSetValid() {
        if (binding.etPin.getOTP().isEmpty() || binding.etPin.getOTP().length() < 4)
        {
            binding.lblError.setText(getResources().getString(R.string.txt_enter_pin));
            return false;
        }
        return true;
    }
    private boolean isValid() {

        if (binding.etPin.getOTP().isEmpty() || binding.etPin.getOTP().length() < 4)
        {
            binding.lblError.setText(getResources().getString(R.string.txt_enter_pin));
            return false;
        }
        else if (!binding.etPin.getOTP().equalsIgnoreCase(LeafPreference.getInstance(this).getString(LeafPreference.PIN)))
        {
            binding.lblError.setText(getResources().getString(R.string.txt_enter_valid_pin));
            return false;
        }
        return true;
    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
      //  binding.progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_JOIN_GROUP) {
            AppLog.e("UserExist->", "join group api response");
            showLoadingBar(binding.progressBar);
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