package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
import school.campusconnect.databinding.ActivityChangeLanguageBinding;

public class ChangeLanguageActivity extends BaseActivity {

    @Bind(R.id.rbEnglish)
    RadioButton rbEnglish;

    @Bind(R.id.rbKannada)
    RadioButton rbKannada;

    ActivityChangeLanguageBinding binding;

    private boolean isSplash = false;
    private boolean isDashboard = false;
    private boolean isEnableBack = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_change_language);
        ButterKnife.bind(this);
        
        if (getIntent() != null)
        {
            isSplash = getIntent().getBooleanExtra("isSplash",false);
            isDashboard = getIntent().getBooleanExtra("isDashboard",false);
            isEnableBack = getIntent().getBooleanExtra("enableBack",false);
        }

        rbEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbEnglish.isChecked())
                {
                    rbKannada.setChecked(false);
                }
                else
                {
                    rbKannada.setChecked(true);
                }

            }
        });

        rbKannada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbKannada.isChecked())
                {
                    rbEnglish.setChecked(false);
                }
                else
                {
                    rbEnglish.setChecked(true);
                }
            }
        });


        if(isSplash)
        {
            if (isEnableBack)
            {
                binding.imgBack.setVisibility(View.VISIBLE);
            }
            else
            {
                binding.imgBack.setVisibility(View.GONE);
            }
        }
        else {

            binding.imgBack.setVisibility(View.VISIBLE);
        }

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


        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rbEnglish.isChecked())
                {
                    updateViews("en");
                }
                else if (rbKannada.isChecked())
                {
                    updateViews("kn");
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

        if (isSplash)
        {
            Bundle b = new Bundle();
            b.putBoolean("enableBack",true);
            gotoActivity(LoginActivity2.class,b,false);
        }
        else if (isDashboard)
        {
            gotoActivity(GroupDashboardActivityNew.class,null,true);
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