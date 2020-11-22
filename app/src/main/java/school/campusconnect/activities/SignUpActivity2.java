package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import school.campusconnect.utils.AppLog;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.SignUpRequest;
import school.campusconnect.datamodel.SignupValidationError;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;

public class SignUpActivity2 extends BaseActivity implements LeafManager.OnAddUpdateListener<SignupValidationError> {

    @Bind(R.id.layout_signup_name)
    EditText edtName;

    @Bind(R.id.layout_signup_email)
    EditText edtEmail;

    @Bind(R.id.tvEmailLbl)
    TextView tvEmailLbl;

    @Bind(R.id.btnNext)
    Button btnNext;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    String pNumber;
    String countryCode;
    String countryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        ButterKnife.bind(this);

        pNumber = LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.phoneNumber);
        AppLog.e("TAG", "Number is " + pNumber);
        countryCode = "IN";

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            countryCode = intent.getExtras().getString("countryCode", "IN");
            countryName = intent.getExtras().getString("Country", "");
        }

        if (countryName.equalsIgnoreCase("India")) {
            tvEmailLbl.setVisibility(View.GONE);
            edtEmail.setVisibility(View.GONE);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignUp();
            }
        });

        edtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnNext.performClick();
                    handled = true;
                }
                return handled;
            }
        });
    }

    public boolean isValid() {


        if (!isValueValid(edtName)) {// || !isValueValid(edtEmail/*.editText*/)) {
            return false;
        }

        return true;
    }

    public void doSignUp() {

        if (isConnectionAvailable()) {

            if (isValid()) {
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
                LeafManager manager = new LeafManager();
                SignUpRequest request = new SignUpRequest();
                request.name = edtName.getText().toString();
                request.phone = pNumber;
                request.countryCode = countryCode;
                btnNext.setEnabled(false);
                manager.doSignUp(this, request, Constants.group_category);
                AppLog.e("TAG", new Gson().toJson(request));

            }
        } else {

            showNoNetworkMsg();
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        btnNext.setEnabled(true);

        //hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

        Intent i = new Intent(this, UserExistActivity.class);
        startActivity(i);
        finish();

        btnNext.setEnabled(true);

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<SignupValidationError> error) {
        btnNext.setEnabled(true);
        //hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onException(int apiId, String error) {
        btnNext.setEnabled(true);
        //hideLoadingDialog();
        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(SignUpActivity2.this, error, Toast.LENGTH_SHORT).show();
    }
}
