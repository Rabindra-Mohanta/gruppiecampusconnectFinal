package school.campusconnect.activities;

import android.os.Bundle;

import butterknife.OnClick;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.ChangePasswordResponse;
import school.campusconnect.utils.AppLog;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ChangePasswordRequest;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.PasswordValidationError;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.PassWordMask;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener, LeafManager.OnAddUpdateListener<PasswordValidationError> {
    private static final String TAG = "ChangePasswordActivity";
    @Bind(R.id.btn_login)
    Button btn_login;

    @Bind(R.id.layout_password)
    EditText layout_password;

    @Bind(R.id.layout_password_old)
    EditText layout_password_old;


    @Bind(R.id.layout_password_conf)
    EditText layout_password_conf;

    @Bind(R.id.imgEye)
    ImageView imgEye;

    @Bind(R.id.imgEye2)
    ImageView imgEye2;

    @Bind(R.id.imgEyeOld)
    ImageView imgEyeOld;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private boolean show;
    private boolean show2;
    private boolean showOld;
    private LeafManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_password_change);
        ButterKnife.bind(this);

        manager = new LeafManager();

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
                ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
                changePasswordRequest.oldPassword = layout_password_old.getText().toString();
                changePasswordRequest.newPasswordFirst = layout_password.getText().toString();
                changePasswordRequest.newPasswordSecond = layout_password_conf.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                hide_keyboard();
                manager.changePassword(this, changePasswordRequest);
            } else {
                showNoNetworkMsg();
            }
        }
    }

    private boolean isValid() {
        if (!isValueValid(layout_password_old))
            return false;
        else if (!isValueValid(layout_password))
            return false;
        if(layout_password.getText().toString().length()<6){
            Toast.makeText(this, getResources().getString(R.string.lbl_new_password_validation), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValueValid(layout_password_conf))
            return false;
        else if (!layout_password.getText().toString().matches(layout_password_conf.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.toast_password_not_match), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        ChangePasswordResponse changePasswordResponse= (ChangePasswordResponse) response;
        LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.TOKEN, changePasswordResponse.token);
        finish();
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<PasswordValidationError> error) {
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        AppLog.e(TAG, "onFailure : " + error);
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.imgEye, R.id.imgEye2, R.id.imgEyeOld})
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
            case R.id.imgEyeOld:
                if (showOld) {
                    imgEyeOld.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_hide));
                    layout_password_old.setTransformationMethod(new PassWordMask());
                    showOld = false;

                } else {
                    imgEyeOld.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_show));
                    layout_password_old.setTransformationMethod(null);
                    showOld = true;
                }
                break;


        }
    }
}
