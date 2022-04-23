package school.campusconnect.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;

import school.campusconnect.utils.AppLog;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Locale;

import school.campusconnect.BuildConfig;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.database.RememberPref;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.numberexist.NumberExistRequest;
import school.campusconnect.datamodel.numberexist.NumberExistResponse;
import school.campusconnect.datamodel.versioncheck.VersionCheckResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.Constants;
import school.campusconnect.views.SMBDialogUtils;

public class LoginActivity2 extends BaseActivity implements LeafManager.OnCommunicationListener {

    private static final String TAG = "LoginActivity2";

    @Bind(R.id.layout_country)
    EditText edtCountry;

    @Bind(R.id.layout_number)
    EditText edtNumber;

   /* @Bind(R.id.rgLanguage)
    RadioGroup rgLanguage;*/

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.btnNext)
    Button btnNext;

    @Bind(R.id.imgBack)
    ImageView imgBack;

    String countryCode;
    String countryName;

    public static LoginActivity2 loginActivity2;

    int currentCountry;
    int version;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ButterKnife.bind(this);

        setlinks();

        loginActivity2 = this;
        currentCountry = 1;
        try {
            countryName = getCountryNum(getUserCountry(getApplicationContext())).toUpperCase();
        } catch (Exception e) {
            countryName = "India";
        }

        String[] country = getResources().getStringArray(R.array.array_country);

        for (int i = 0; i < country.length; i++) {
            if (countryName.equalsIgnoreCase(country[i])) {
                currentCountry = i + 1;
                countryName = country[i];
                break;
            }
        }

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        edtCountry.setText(countryName);
        edtCountry.setFocusable(false);


        if (getIntent() != null && getIntent().getExtras() != null &&  getIntent().getExtras().getBoolean("enableBack"))
        {
            imgBack.setVisibility(View.VISIBLE);
        }
        else
        {
            imgBack.setVisibility(View.GONE);
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity2.this, ChangeLanguageActivity.class);
                intent.putExtra("isSplash",true);
                intent.putExtra("enableBack",true);
                startActivity(intent);
            }
        });
        edtCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMBDialogUtils.showSMBSingleChoiceDialog(LoginActivity2.this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
            }
        });

        edtCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                SMBDialogUtils.showSMBSingleChoiceDialog(LoginActivity2.this, R.string.title_country, R.array.array_country, currentCountry - 1,
                        new SMBDailogClickListener(R.id.layout_country));
            }
        });

        if (RememberPref.getInstance(this).contains(RememberPref.REMEMBER_USERNAME)) {
            String number = RememberPref.getInstance(this).getString(RememberPref.REMEMBER_USERNAME);
            edtNumber.setText(number);
        }

        edtNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                AppLog.e(TAG,"click...1  : "+actionId);
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnNext.performClick();
                    handled = true;
                }
                return handled;
            }
        });


       /* rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i)
                {
                    case R.id.rbEnglish:
                        updateViews("en");
                        break;

                    case R.id.rbKannada:
                        updateViews("kn");
                        break;
                }
            }
        });*/
    }

    private void updateViews(String languageCode) {
        Log.e(TAG,"language Code"+languageCode);
        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Locale.Helper.Selected.Language", languageCode);
        Log.e("LocaleHelper","language "+languageCode);
        editor.apply();
        gotoActivity(LoginActivity2.class,null,true);
    }

    public void gotoActivity(Class className, Bundle bundle, boolean isClearStack) {
        Intent intent = new Intent(this, className);

        if (bundle != null)
            intent.putExtras(bundle);

        if (isClearStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivity(intent);
    }

    private void setlinks() {
        TextView txtTerms = (TextView) findViewById(R.id.txtTerms);

        SpannableString ss = new SpannableString("By continuing, you agree to Terms and Conditions and Privacy Policy");
        ClickableSpan clickableSpanTerm = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gruppie.in/terms"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ClickableSpan clickableSpanPrivacy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gruppie.in/privacy/policy"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpanTerm, 28, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpanPrivacy, 53, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtTerms.setText(ss);
        txtTerms.setMovementMethod(LinkMovementMethod.getInstance());
        txtTerms.setLinkTextColor(Color.WHITE);
    }

    @OnClick({R.id.btn_next, R.id.btnNext, R.id.txt_signup})
    public void onClick(View view) {
        hide_keyboard();

        switch (view.getId()) {

            case R.id.btn_next:
            case R.id.btnNext:
                if (isConnectionAvailable()) {

                    if (isValid() && permissionCheck()) {

                        loginApi();
                    }

                } else {
                    showNoNetworkMsg();
                }
                break;

            case R.id.txt_signup:

                Intent intent = new Intent(LoginActivity2.this, SignUpActivity2.class);
                startActivity(intent);
                //finish();

                break;


        }
    }

    private void loginApi() {
        btnNext.setEnabled(false);
        if (progressBar != null)
            showLoadingBar(progressBar);
         //   progressBar.setVisibility(View.VISIBLE);

        if (RememberPref.getInstance(this).contains(RememberPref.REMEMBER_USERNAME)) {
            String number = RememberPref.getInstance(this).getString(RememberPref.REMEMBER_USERNAME);
            if (!number.equals(edtNumber.getText().toString())) {
                RememberPref.getInstance(this).remove(RememberPref.REMEMBER_USERNAME);
                RememberPref.getInstance(this).remove(RememberPref.REMEMBER_PASSWORD);
            }
        }

        LeafManager manager = new LeafManager();
        NumberExistRequest request = new NumberExistRequest();
        request.phone = edtNumber/*.editText*/.getText().toString();

        String[] str = getResources().getStringArray(R.array.array_country_values);
        request.countryCode = str[currentCountry - 1];
        countryCode = str[currentCountry - 1];

        AppLog.e("Login2", "Post Data :" + new Gson().toJson(request));
        manager.doNext(this, request);
    }

    private boolean permissionCheck() {
       /* if (!MixOperations.hasPermission(this, new String[]{Manifest.permission.RECEIVE_SMS})) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
            return false;
        }*/
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loginApi();
        }
    }

    public class SMBDailogClickListener implements DialogInterface.OnClickListener {
        private int DIALOG_ID = -1;

        @Override
        public void onClick(DialogInterface dialog, int which) {

            ListView lw = ((AlertDialog) dialog).getListView();

            switch (DIALOG_ID) {

                case R.id.layout_country:
                    edtCountry/*.editText*/.setText(lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString());
                    currentCountry = lw.getCheckedItemPosition() + 1;
                    countryName = lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString();
                    break;


            }
        }

        public SMBDailogClickListener(int id) {
            DIALOG_ID = id;
        }
    }

    private boolean isValid() {

        boolean isValid = true;

        String phoneNumber = edtNumber/*.editText*/.getText().toString();
        String country = edtCountry/*.editText*/.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)) {

            edtNumber.requestFocus();
            edtNumber/*.editText*/.setError("Enter Your Mobile Number");
            isValid = false;
            return isValid;

        }
        if (TextUtils.isEmpty(country)) {

            edtCountry.requestFocus();
            edtCountry/*.editText*/.setError("Select a countryCode to proceed");
            isValid = false;
            return isValid;
        }

        return isValid;
    }


    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getNetworkCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM countryCode code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network countryCode code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public String getCountryNum(String code) {
        Locale loc = new Locale("", code);
        return loc.getDisplayCountry();
    }


    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        btnNext.setEnabled(true);
        // hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_VERSION) {
            VersionCheckResponse res = (VersionCheckResponse) response;

            AppLog.e("VERSION", "version " + res.data.version + ">" + version);
            if (res.data.version > version) {
                showUpdate(res.data.message);
            }
        } else {
            NumberExistResponse res = (NumberExistResponse) response;

            AppLog.e("TAG", "Response is : " + new Gson().toJson(response));

            if (!res.data.isAllowedToAccessApp) {
                Toast.makeText(loginActivity2, getResources().getString(R.string.toast_you_are_not_authorized), Toast.LENGTH_SHORT).show();
                return;
            }

            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.countryCode, res.data.countryCode);
            LeafPreference.getInstance(getApplicationContext()).setString(LeafPreference.phoneNumber, res.data.phone);

            if (res.data.isUserExist) {

                Intent i = new Intent(LoginActivity2.this, UserExistActivity.class);
                i.putExtra("passwordChanged", res.data.passwordChanged);
                i.putExtra("fromLogin", true);
                startActivity(i);

            } else {
                startActivity(new Intent(this, SignUpActivity2.class).putExtra("Country", countryName).putExtra("countryCode", countryCode));
                AppLog.e("TAG", "SignUpActivity2 Launched");
            }
        }
        btnNext.setEnabled(true);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);
        btnNext.setEnabled(true);
    }

    @Override
    public void onException(int apiId, String msg) {
        //hideLoadingDialog();
        if (progressBar != null)
            hideLoadingBar();
          //  progressBar.setVisibility(View.GONE);
        btnNext.setEnabled(true);
    }

    public void showUpdate(String msg) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_update);

        TextView btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);

        TextView btn_cancel = (TextView) dialog.findViewById(R.id.btn_cancel);

        final TextView txt_text = (TextView) dialog.findViewById(R.id.txt_text);

        txt_text.setText(msg);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
