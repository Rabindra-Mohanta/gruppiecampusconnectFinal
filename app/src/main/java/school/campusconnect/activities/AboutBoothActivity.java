package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityAboutBoothBinding;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.UploadCircleImageFragment;

public class AboutBoothActivity extends BaseActivity {

    ActivityAboutBoothBinding binding;
    public static final String TAG = "AboutBoothActivity";
    private MyTeamData classData;

    UploadCircleImageFragment imageFragment;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_about_booth);
        init();
    }

    private void init()
    {

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);


        imageFragment = UploadCircleImageFragment.newInstance(null, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, imageFragment).commit();
        getSupportFragmentManager().executePendingTransactions();

        if (getIntent() != null)
        {
            classData = new Gson().fromJson(getIntent().getStringExtra("class_data"), MyTeamData.class);
            AppLog.e(TAG, "classData : " + classData);
        }

        setTitle(classData.name);
        binding.tvBoothName.setText(classData.name);
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (classData.userImage != null && !classData.userImage.isEmpty())
        {
            imageFragment.updatePhotoFromUrl(classData.userImage);
        }
        else
        {
            imageFragment.setInitialLatterImage(classData.name);
        }
    }
}