package school.campusconnect.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.database.RememberPref;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.ForgotPasswordRequest;
import school.campusconnect.datamodel.ForgotPasswordValidationError;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.LoginRequest;
import school.campusconnect.datamodel.LoginResponse;
import school.campusconnect.datamodel.OtpVerifyReq;
import school.campusconnect.datamodel.OtpVerifyRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.AppSignatureHelper;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.PassWordMask;

public class UserExistActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<ForgotPasswordValidationError>, LeafManager.OnCommunicationListener {

    private static final String TAG = "UserExistActivity";
    @Bind(R.id.layout_password)
    EditText edtPassword;

    @Bind(R.id.imgEye)
    ImageView imgEye;

    @Bind(R.id.llOptSend)
    LinearLayout llOtpSend;

    @Bind(R.id.tvGenerateOTP)
    TextView txtGetOtp;

    @Bind(R.id.tvOtpSendMsg)
    TextView tvOtpSendMsg;


    @Bind(R.id.chk)
    CheckBox checkBox;


    @Bind(R.id.txtCountDown)
    TextView tvCountDown;

    @Bind(R.id.btn_login)
    Button btnLogin;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    String phoneNumber;
    String countryCode;

    SmsVerifyCatcher smsVerifyCatcher;

    static UserExistActivity userExistActivity;
    private CountDownTimer myCountDown;

    static int count;

    CleverTapAPI cleverTap;

    boolean isIndia;
    private LeafManager manager;
    private boolean show = false;
    private boolean fromLogin;
    private ArrayList<String> listKey;
    private Boolean validateUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_exist);
        ButterKnife.bind(this);
        userExistActivity = this;

        listKey = new AppSignatureHelper(this).getAppSignatures();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            fromLogin = intent.getBooleanExtra("fromLogin", false);
            validateUser = intent.getBooleanExtra("userFlag",false);
        }

        AppLog.e("fromLogin", "is " + fromLogin);

        initObjects();

        count = 0;

        phoneNumber = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.phoneNumber);
        countryCode = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.countryCode);

        tvOtpSendMsg.setText(getResources().getString(R.string.toast_otp_send_to) + phoneNumber);

        isIndia = countryCode.equals("IN");


        AppLog.e("TESTSUCC", "countryCode " + countryCode);
        AppLog.e("TESTSUCC", "isIndia " + isIndia);

        txtGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOtp();

            }
        });

        if (RememberPref.getInstance(this).contains(RememberPref.REMEMBER_PASSWORD)) {
            String pass = RememberPref.getInstance(this).getString(RememberPref.REMEMBER_PASSWORD);
            edtPassword.setText(pass);
        }
        if (!fromLogin) {
            checkBox.setVisibility(View.INVISIBLE);
            txtGetOtp.setText(getResources().getString(R.string.lbl_generate_otp));
            edtPassword.setHint(getResources().getString(R.string.hint_password));
            getOtp();
        } else {
            txtGetOtp.setText(getResources().getString(R.string.lbl_forgot_pwd_q));
            edtPassword.setHint(getResources().getString(R.string.hint_pass));
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromLogin)
                    login();
                else
                    verifyOtp();
            }
        });


        myCountDown = new CountDownTimer((1 * 30) * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long sec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                tvCountDown.setText(sec + "");
            }

            @Override
            public void onFinish() {
                tvCountDown.setText("");
                txtGetOtp.setEnabled(true);
                txtGetOtp.setTextColor(getResources().getColor(R.color.colorTextWhite));
                llOtpSend.setVisibility(View.INVISIBLE);
            }
        };

        edtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                AppLog.e(TAG, "click...1  : " + actionId);
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick();
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void verifyOtp() {

        if (isConnectionAvailable()) {

            if (!isValueValid(edtPassword))
                return;
            if (progressBar != null)
                showLoadingBar(progressBar,true);
            //    progressBar.setVisibility(View.VISIBLE);
            btnLogin.setTextColor(getResources().getColor(R.color.grey));
            btnLogin.setEnabled(false);

            LeafManager manager = new LeafManager();
            OtpVerifyReq otpVerifyReq = new OtpVerifyReq();
            otpVerifyReq.countryCode = countryCode;
            otpVerifyReq.phone = phoneNumber;
            otpVerifyReq.otp = edtPassword.getText().toString();
            manager.otpVerify(this, otpVerifyReq);
        } else {
            showNoNetworkMsg();
        }
    }

    @OnClick({R.id.imgEye})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgEye:
                if (show) {
                    imgEye.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_hide));
                    edtPassword.setTransformationMethod(new PassWordMask());
                    show = false;

                } else {
                    imgEye.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_show));
                    edtPassword.setTransformationMethod(null);
                    show = true;
                }
                break;
        }
    }

    private void initObjects() {
        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
            AppLog.e(TAG, "Success to found cleverTap objects=>");
        } catch (CleverTapMetaDataNotFoundException e) {
            AppLog.e(TAG, "CleverTapMetaDataNotFoundException=>" + e.toString());
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            AppLog.e(TAG, "CleverTapPermissionsNotSatisfied=>" + e.toString());
            // thrown if you haven???t requested the required permissions in your AndroidManifest.xml
        }
    }

    public static UserExistActivity getInstance() {
        return userExistActivity;
    }

    public void recivedSms(String message) {
        try {
            edtPassword/*.editText*/.setText("");
            edtPassword/*.editText*/.setText(message);
            if (fromLogin)
                login();
            else
                verifyOtp();
        } catch (Exception e) {
            AppLog.e("BROADCAST", "method catch called\n" + e.getMessage());
        }
    }

    public void login() {

        if (checkBox.isChecked()) {
            RememberPref.getInstance(this).setString(RememberPref.REMEMBER_USERNAME, phoneNumber);
            RememberPref.getInstance(this).setString(RememberPref.REMEMBER_PASSWORD, edtPassword/*.editText*/.getText().toString());
        } else {
            RememberPref.getInstance(this).remove(RememberPref.REMEMBER_USERNAME);
            RememberPref.getInstance(this).remove(RememberPref.REMEMBER_PASSWORD);
        }

        if (isConnectionAvailable()) {

            if (!isValueValid(edtPassword))
                return;
            if (progressBar != null)
                showLoadingBar(progressBar,true);
                //progressBar.setVisibility(View.VISIBLE);
            btnLogin.setTextColor(getResources().getColor(R.color.grey));
            btnLogin.setEnabled(false);

            LeafManager manager = new LeafManager();
            LoginRequest request = new LoginRequest();
            request.userName.countryCode = countryCode;
            request.userName.phone = phoneNumber;
            request.deviceType = "Android";
            request.udid = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                request.appVersion = pInfo.versionCode + "";
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            request.osVersion = Build.VERSION.SDK_INT + "";
            request.deviceModel = Build.MODEL;
            request.deviceToken = LeafPreference.getInstance(UserExistActivity.this).getString(LeafPreference.GCM_TOKEN);
            request.password = edtPassword/*.editText*/.getText().toString();
            AppLog.e("Login..", "data is " + new Gson().toJson(request));

            LeafPreference.getInstance(this).setString(LeafPreference.LOGIN_REQ,new Gson().toJson(request));

            manager.doLogin(this, request);

        } else {

            showNoNetworkMsg();
        }
    }
    private void subScribeUser() {
      /*  FirebaseMessaging.getInstance().subscribeToTopic(LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.LOGIN_ID))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AppLog.e(TAG, "subscribeToTopic : " + LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.LOGIN_ID)+ " : Successful()");
                        } else {
                            AppLog.e(TAG, "subscribeToTopic : " + LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.LOGIN_ID) + " Fail()");
                        }

                    }
                });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
//        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //      smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (ArrayIndexOutOfBoundsException e) {
            AppLog.e("onRequestPermissions", "error is " + e.toString());
        }
    }

    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    public void getOtp() {
        AppLog.e(TAG, "getOtp called");

        if (isConnectionAvailable()) {

            hide_keyboard();
            //showLoadingDialog();
            if (progressBar != null)
                showLoadingBar(progressBar,true);
                //progressBar.setVisibility(View.VISIBLE);
            LeafManager manager = new LeafManager();
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.countryCode = countryCode;
            request.phone = phoneNumber;
            if (listKey != null && listKey.size() > 0) {
                request.smsKey = listKey.get(0);
            }

            count++;
            if (count == 4)
                count = 1;

            manager.forgetPassword(this, request, count);
            txtGetOtp.setEnabled(false);
            txtGetOtp.setTextColor(getResources().getColor(R.color.colorTextLight));
        } else {

            showNoNetworkMsg();
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        btnLogin.setTextColor(getResources().getColor(R.color.white));
        btnLogin.setEnabled(true);

        if (apiId == LeafManager.API_ID_FORGOT_PWD) {

            //hideLoadingDialog();
            if (progressBar != null)
                hideLoadingBar();
            //    progressBar.setVisibility(View.GONE);

            if (isIndia) {
                AppLog.e("TESTSUCC", "if");
                fromLogin = false;
                edtPassword.setHint(getResources().getString(R.string.hint_password));
                checkBox.setVisibility(View.INVISIBLE);
                txtGetOtp.setText(getResources().getString(R.string.lbl_generate_otp));
                myCountDown.start();
                txtGetOtp.setEnabled(false);
                txtGetOtp.setTextColor(getResources().getColor(R.color.colorTextLight));
                llOtpSend.setVisibility(View.VISIBLE);
                Toast.makeText(this, getResources().getString(R.string.toast_otp_sent_mobile_success), Toast.LENGTH_LONG).show();
            } else {
                AppLog.e("TESTSUCC", "else");
                txtGetOtp.setEnabled(true);
                txtGetOtp.setTextColor(getResources().getColor(R.color.colorTextWhite));
                llOtpSend.setVisibility(View.INVISIBLE);
                Toast.makeText(this, getResources().getString(R.string.toast_otp_send_email_success), Toast.LENGTH_LONG).show();
            }
            smsListener();
        } else if (apiId == LeafManager.API_ID_LOGIN) {
            //hideLoadingDialog();
           /* if (progressBar != null)
                progressBar.setVisibility(View.GONE);*/
            LoginResponse response1 = (LoginResponse) response;
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.LOGIN_ID, response1.userId);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.ROLE, response1.role);
           // LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.TOKEN, response1.token);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.NAME, response1.name);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.NUM, response1.phone);

            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PROFILE_IMAGE_NEW, response1.image);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PROFILE_NAME, response1.name);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PROFILE_VOTERID, response1.voterId);

            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.CALLING_CODE, response1.counryTelephoneCode);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.COUNTRY_CODE, response1.countryAlpha2Code);

            AppLog.e("LOGIN", "id is " + response1.userId);
            AppLog.e("LOGIN", "token is " + response1.token);
            AppLog.e("LOGIN", "name is " + response1.name);
            AppLog.e("LOGIN", "image is " + response1.image);

            addCleverTapProfile(response1);

            subScribeUser();

            hide_keyboard();

            if (BuildConfig.AppCategory.equalsIgnoreCase("CAMPUS") && BuildConfig.AppName.equalsIgnoreCase("GC2"))
            {
                if (response1.groupCount == 0) {
                    LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.TOKEN, response1.token);
                    LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.GROUP_ID, response1.groupId);
                    LeafPreference.getInstance(getApplicationContext()).setInt(LeafPreference.GROUP_COUNT, response1.groupCount);

                    Intent register = new Intent(this, RegisterInstituteActivity.class);
                    startActivity(register);
                    finish();
                    return;
                }
            }

            Intent i = new Intent(getApplicationContext(),LoginPinActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("Role",response1.role);
            i.putExtra("token",response1.token);
            i.putExtra("groupCount",String.valueOf(response1.groupCount));
            i.putExtra("groupID",response1.groupId);
            startActivity(i);
            finish();



          /*  if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {

                LeafPreference.getInstance(getApplicationContext()).setInt(LeafPreference.CONST_GROUP_COUNT, response1.groupCount);

                if (LeafPreference.getInstance(getApplicationContext()).getInt(LeafPreference.CONST_GROUP_COUNT) > 1) {
                    Intent login = new Intent(this, ConstituencyListActivity.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                    finish();
                } else {
                    manager = new LeafManager();
                    manager.getGroupDetail(this, response1.groupId);
                }
            } else {
                if ("CAMPUS".equalsIgnoreCase(BuildConfig.AppCategory)) {
                    AppLog.e("UserExist->", "join group api called");
                    LeafPreference.getInstance(getApplicationContext()).setInt(LeafPreference.GROUP_COUNT, response1.groupCount);
                    if ("taluk".equalsIgnoreCase(response1.role)) {
                        Intent login = new Intent(this, TalukListActivity.class);
                        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(login);
                        finish();
                    } else {
                        if (response1.groupCount > 1) {
                            Intent login = new Intent(this, Home.class);
                            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                            finish();
                        } else {
                            manager = new LeafManager();
                            manager.getGroupDetail(this, response1.groupId);
                        }
                    }
                } else {
                    AppLog.e("UserExist->", "join Direct group api called");
                    manager = new LeafManager();

                    manager.joinGroupDirect(this, BuildConfig.APP_ID);
                }
            }*/
        }
        if (apiId == LeafManager.API_JOIN_GROUP) {
            AppLog.e("UserExist->", "join group api response");
            if (progressBar != null)
                hideLoadingBar();
               // progressBar.setVisibility(View.GONE);

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

        if (apiId == LeafManager.API_ID_OTP_VERIFY) {
            if (progressBar != null)
                hideLoadingBar();
               // progressBar.setVisibility(View.GONE);

            OtpVerifyRes otpVerifyRes = (OtpVerifyRes) response;
            if (otpVerifyRes.data != null && otpVerifyRes.data.otpVerified) {
                Intent intent = new Intent(this, NewPassActivity.class);
                intent.putExtra("otp", edtPassword.getText().toString());
                intent.putExtra("userFlag", validateUser);
                startActivity(intent);
                finish();
            } else {
                edtPassword.setError(getResources().getString(R.string.toast_invalid_otp));
                edtPassword.requestFocus();
                //Toast.makeText(userExistActivity, getResources().getString(R.string.toast_invalid_otp), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void smsListener() {
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AppLog.e("smsListener", "onSuccess");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppLog.e("smsListener", "onFailure  :" + e.toString());
            }
        });
    }

    private void addCleverTapProfile(LoginResponse loginResponse) {
        HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
        profileUpdate.put("Identity", loginResponse.userId);                    // String or phone
        profileUpdate.put("Name", loginResponse.name);                  // String
        profileUpdate.put("Phone", loginResponse.phone);
        profileUpdate.put("Photo", loginResponse.image);                // Phone (with the countryCode code, starting with +)
        if (cleverTap != null) {
            cleverTap.profile.push(profileUpdate);
            AppLog.e(TAG, "CleverTap profile added.");

            profileUpdate.remove("Photo");
            cleverTap.event.push("Login", profileUpdate);
            AppLog.e(TAG, "CleverTap Login added.");
        } else {
            AppLog.e(TAG, "CleverTap Profile & login not added.");
        }


    }

    @Override
    public void onFailure(int apiId, String msg) {

        btnLogin.setTextColor(getResources().getColor(R.color.white));
        btnLogin.setEnabled(true);

        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
            //progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_JOIN_GROUP) {
            AppLog.e("UserExist-> on Failure", "join group api response");

            AppLog.e("UserExist->", "getGroupDetail api called");
            manager.getGroupDetail(this, BuildConfig.APP_ID);

        } else {
            edtPassword.setError(getResources().getString(R.string.toast_invalid_otp_password));
            edtPassword.requestFocus();
            //Toast.makeText(this, getResources().getString(R.string.toast_invalid_otp_password), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<ForgotPasswordValidationError> error) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
          //  progressBar.setVisibility(View.GONE);
        ForgotPasswordValidationError fieldErrors = error.errors.get(0);
    }

    @Override
    public void onException(int apiId, String error) {

        btnLogin.setTextColor(getResources().getColor(R.color.white));
        btnLogin.setEnabled(true);

        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
