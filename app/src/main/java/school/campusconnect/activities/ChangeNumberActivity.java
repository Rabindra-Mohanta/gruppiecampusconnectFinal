package school.campusconnect.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ChangeNumberRequest;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.NumberValidationError;
import school.campusconnect.network.LeafManager;
import school.campusconnect.views.DrawableEditText;
import school.campusconnect.views.SMBDialogUtils;

public class ChangeNumberActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<NumberValidationError>, View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Bind(R.id.layout_country)
    DrawableEditText edtCountry;

    @Bind(R.id.layout_email)
    DrawableEditText edtEmail;

    @Bind(R.id.layout_new_number)
    DrawableEditText edtNewNumber;
    @Bind(R.id.btn_update)
    ImageView imgUpdate;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    int currentCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_number_change);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.action_change_number);

        currentCountry = 1;

        edtCountry.editText.setOnClickListener(this);
        edtCountry.editText.setFocusable(false);

        String[] str = getResources().getStringArray(R.array.array_country);
        edtCountry.editText.setText(str[0]);

        // edtEmail.editText.setText(LeafPreference.getInstance(getApplicationContext()).getString(LeafPreference.EMAIL));

        imgUpdate.setOnClickListener(this);

    }

   /* @OnClick(R.id.btn_change_number)
    public void onChangeNumberClick() {
        if (isConnectionAvailable()) {
            if (isValid()) {
                showLoadingDialog();
                LeafManager manager = new LeafManager();
                ChangeNumberRequest request = new ChangeNumberRequest();
                request.phone.phone = edtNewNumber.editText.getText().toString();
                String[] str = getResources().getStringArray(R.array.array_country_values);
                request.phone.countryCode = str[currentCountry - 1];
                request.email = edtEmail.editText.getText().toString();
                manager.changeNumber(this, request);
            }
        } else {
            showNoNetworkMsg();
        }
    }*/

    private boolean isValid() {
        boolean valid = true;
//        if (edtPwd.editText.getText().toString().length() < 8) {
//            edtPwd.editText.setError(getString(R.string.msg_valid_pwd));
//            valid = false;
//        }
        if (!isValueValid(edtNewNumber.editText)) {
            valid = false;
        } else if (edtNewNumber.editText.getText().toString().length() < 10) {
            edtNewNumber.editText.setError(getString(R.string.msg_valid_phone));
            valid = false;
        } /*else if (currentCountry != 1) {
           AppLog.e("Check", "3 called");

            if (edtEmail.editText.getText().toString().isEmpty()) {
                edtEmail.editText.setError(getString(R.string.msg_mandatory_email));
                valid = false;

            } else if (!isValidEmail(edtEmail.editText.getText().toString())) {
                edtEmail.editText.setError(getString(R.string.msg_valid_email));
                edtEmail.editText.requestFocus();
                valid = false;
            }
        } else {
           AppLog.e("Check", "Else called");
            if (!edtEmail.editText.getText().toString().isEmpty()) {
                if (!isValidEmail(edtEmail.editText.getText().toString())) {
                    edtEmail.editText.setError(getString(R.string.msg_valid_email));
                    edtEmail.editText.requestFocus();
                    valid = false;
                }
            }
        }*/
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        // hideLoadingDialog();
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);
        edtNewNumber.editText.setText("");
        Toast.makeText(this, getResources().getString(R.string.toast_number_change_successfully), Toast.LENGTH_LONG).show();
        logout();
    }

    /*private void logout()
    {
       AppLog.e("Logout", "onSuccessCalled");
        ChangePasswordResponse cpr = (ChangePasswordResponse) response;
        //LeafPreference.getInstance(ChangePasswordActivity.this).clearData();
        LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.TOKEN, cpr.token);
        *//*Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*//*
        onBackPressed();
    }*/

    @Override
    public void onFailure(int apiId, ErrorResponseModel<NumberValidationError> error) {
        //   hideLoadingDialog();
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);

        AppLog.e("ChangeNumberActivity", "onFailure  ,, msg : " + error);
        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if(error.errors==null)
                return;

            if (error.errors.get(0).phone!=null) {
                edtNewNumber.editText.setError(error.errors.get(0).phone);
                edtNewNumber.editText.requestFocus();
            }
        }



    }

    @Override
    public void onException(int apiId, String msg) {
        //hideLoadingDialog();
        if(progressBar!=null)
            progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        AppLog.e("ChangePass", "Clicked ");
        switch (v.getId())
        {
            case R.id.btn_update:
                if (isConnectionAvailable()) {
                    if (isValid()) {
                        //  showLoadingDialog();
                        if(progressBar!=null)
                            progressBar.setVisibility(View.VISIBLE);
                        LeafManager manager = new LeafManager();
                        ChangeNumberRequest request = new ChangeNumberRequest();
                        request.phone = edtNewNumber.editText.getText().toString();
                        String[] str = getResources().getStringArray(R.array.array_country_values);
                        request.countryCode = str[currentCountry - 1];
                        manager.changeNumber(this, request);
                    }
                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.editText:
                AppLog.e("SignUpActivity", "Clicked Spinner Country");
                SMBDialogUtils.showSMBSingleChoiceDialog(this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
                break;
        }

    }

    public class SMBDailogClickListener implements DialogInterface.OnClickListener {
        private int DIALOG_ID = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListView lw = ((AlertDialog) dialog).getListView();

            lw.setDivider(null);

            switch (DIALOG_ID) {

                case R.id.layout_country:
                    edtCountry.editText.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    currentCountry = lw.getCheckedItemPosition() + 1;
                    break;

            }
        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }
}
