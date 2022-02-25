package school.campusconnect.activities;

import android.content.Intent;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.LoginRequest;
import school.campusconnect.datamodel.LoginResponse;
import school.campusconnect.datamodel.NewPassReq;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.PassWordMask;

public class NewPassActivity extends BaseActivity {

    private static final String TAG = "NewPassActivity";
    @Bind(R.id.btn_login)
    Button btn_login;

    @Bind(R.id.layout_password)
    EditText layout_password;

    @Bind(R.id.layout_password_conf)
    EditText layout_password_conf;

    @Bind(R.id.imgEye)
    ImageView imgEye;

    @Bind(R.id.imgEye2)
    ImageView imgEye2;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;


    private String otp;
    private boolean show;
    private boolean show2;
    private LeafManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pass);

        ButterKnife.bind(this);

        manager = new LeafManager();

        otp = getIntent().getStringExtra("otp");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewPass();
            }
        });

        layout_password_conf.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btn_login.performClick();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void createNewPass() {
        if (isValid()) {
            if (isConnectionAvailable()) {
                NewPassReq newPassReq = new NewPassReq();
                LoginRequest.UserName userName = new LoginRequest.UserName();
                userName.phone = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.phoneNumber);
                userName.countryCode = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.countryCode);
                newPassReq.userName = userName;
                newPassReq.otp = otp;
                newPassReq.password = layout_password.getText().toString();
                newPassReq.confirmPassword = layout_password_conf.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                manager.newPass(this, newPassReq);
            } else {
                showNoNetworkMsg();
            }
        }
    }

    private boolean isValid() {
        if (!isValueValid(layout_password))
            return false;
        if (layout_password.getText().toString().length() < 6) {
            Toast.makeText(this, "Should be atleast 6 character", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValueValid(layout_password_conf))
            return false;
        if (!layout_password.getText().toString().matches(layout_password_conf.getText().toString())) {
            Toast.makeText(this, "Password Not Match.!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        if (apiId == LeafManager.API_ID_NEW_PASS) {
            LoginResponse response1 = (LoginResponse) response;
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.LOGIN_ID, response1.userId);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.ROLE, response1.role);
         //   LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.TOKEN, response1.token);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.NAME, response1.name);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.NUM, response1.phone);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.PROFILE_IMAGE, response1.image);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.CALLING_CODE, response1.counryTelephoneCode);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.COUNTRY_CODE, response1.countryAlpha2Code);

            AppLog.e("LOGIN", "id is " + response1.userId);
            AppLog.e("LOGIN", "token is " + response1.token);
            AppLog.e("LOGIN", "name is " + response1.name);
            AppLog.e("LOGIN", "image is " + response1.image);

            hide_keyboard();

            Intent i = new Intent(getApplicationContext(),LoginPinActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra("Role",response1.role);
            i.putExtra("token",response1.token);
            i.putExtra("groupCount",response1.groupCount);
            i.putExtra("groupID",response1.groupId);
            startActivity(i);
            finish();

            /*if ("constituency".equalsIgnoreCase(BuildConfig.AppCategory)) {
                LeafPreference.getInstance(getApplicationContext()).setInt(LeafPreference.CONST_GROUP_COUNT, response1.groupCount);
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
            }*/

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

            if (progressBar != null)
                progressBar.setVisibility(View.GONE);

            Intent login = new Intent(this, GroupDashboardActivityNew.class);
            login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            finish();

        }

    }


    @Override
    public void onFailure(int apiId, String msg) {

        //hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onException(int apiId, String error) {

        //hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.imgEye, R.id.imgEye2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgEye:
                if (show) {
                    imgEye.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_hide));
                    layout_password.setTransformationMethod(new PassWordMask());
                    show = false;

                } else {
                    imgEye.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_show));
                    layout_password.setTransformationMethod(null);
                    show = true;
                }
                break;
            case R.id.imgEye2:
                if (show2) {
                    imgEye2.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_hide));
                    layout_password_conf.setTransformationMethod(new PassWordMask());
                    show2 = false;

                } else {
                    imgEye2.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_show));
                    layout_password_conf.setTransformationMethod(null);
                    show2 = true;
                }
                break;

        }
    }
}
