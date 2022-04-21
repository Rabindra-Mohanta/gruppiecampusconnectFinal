package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;

public class ChangeLanguageActivity extends BaseActivity {

    @Bind(R.id.rbEnglish)
    RadioButton rbEnglish;

    @Bind(R.id.rbKannada)
    RadioButton rbKannada;

    private boolean isSplash = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        ButterKnife.bind(this);
        
        if (getIntent() != null)
        {
            isSplash = getIntent().getBooleanExtra("isSplash",false);
        }

        rbEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateViews("en");
            }
        });

        rbKannada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateViews("kn");
            }
        });

        String AppLang = getResources().getConfiguration().locale.getLanguage();
        Log.e("AppLang","AppLang "+ AppLang);

        if (AppLang.equalsIgnoreCase("kn"))
        {
            rbKannada.setChecked(true);
            rbEnglish.setChecked(false);
        }
        else
        {
            rbEnglish.setChecked(true);
            rbKannada.setChecked(false);
        }
    }

    private void updateViews(String languageCode) {

        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Locale.Helper.Selected.Language", languageCode);
        Log.e("LocaleHelper","language "+languageCode);
        editor.apply();

        if (isSplash)
        {
            gotoActivity(LoginActivity2.class,null,true);
        }
        else
        {
            gotoActivity(ConstituencyListActivity.class,null,true);
        }
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


}