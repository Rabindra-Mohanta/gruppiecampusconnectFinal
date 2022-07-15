package school.campusconnect.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.CourseFragment;
import school.campusconnect.fragments.VendorFragment;
import school.campusconnect.network.LeafManager;

public class CourseActivity extends BaseActivity {


    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    private LeafManager leafManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        init();
        CourseFragment courseFragment=new CourseFragment();
        courseFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,courseFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_course));
        leafManager = new LeafManager();
    }
}
