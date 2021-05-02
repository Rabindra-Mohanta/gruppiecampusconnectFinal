package school.campusconnect.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.CompletedTopicStudentFragment;
import school.campusconnect.fragments.ReadUnreadUserFragment;

public class CompletedTopicUserActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_unread_post_users);

        init();

        CompletedTopicStudentFragment galleryFragment=new CompletedTopicStudentFragment();
        galleryFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,galleryFragment).commit();
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_completed_student));
    }
}
