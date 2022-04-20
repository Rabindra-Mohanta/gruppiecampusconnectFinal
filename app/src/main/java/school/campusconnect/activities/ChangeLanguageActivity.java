package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;

public class ChangeLanguageActivity extends BaseActivity {

    @Bind(R.id.rgLanguage)
    RadioGroup rgLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        ButterKnife.bind(this);


        rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        });

    }

    private void updateViews(String languageCode) {

        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Locale.Helper.Selected.Language", languageCode);
        Log.e("LocaleHelper","language "+languageCode);
        editor.apply();
        gotoActivity(ConstituencyListActivity.class,null,true);

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