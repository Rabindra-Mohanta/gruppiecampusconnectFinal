package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.BaseActivity;
import school.campusconnect.fragments.FavouritePostFragment;

public class GcmBookmarksActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_bookmarks);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FavouritePostFragment.newInstance(getIntent().getExtras())).commit();


    }
}