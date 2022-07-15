package school.campusconnect.activities;

import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.AttendancePagerAdapter;
import school.campusconnect.fragments.AttendancePreSchoolListFragment;

public class AttendancePareSchool extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    Fragment[] fragments=new Fragment[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendace_pre_school);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.title_attendance));

        setupViewPager();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_class:
                Intent attendanceIntent = new Intent(this, AttendanceReportActivity.class);
                attendanceIntent.putExtra("team_id", getIntent().getStringExtra("team_id"));
                startActivity(attendanceIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupViewPager() {
        tabLayout.setupWithViewPager(viewPager);
        fragments[0] = new AttendancePreSchoolListFragment();
        Bundle bundle=new Bundle();
        bundle.putString("group_id",getIntent().getStringExtra("group_id"));
        bundle.putString("team_id",getIntent().getStringExtra("team_id"));
        bundle.putString("type","IN");
        fragments[0].setArguments(bundle);
        fragments[1] = new AttendancePreSchoolListFragment();
        Bundle bundle2=new Bundle();
        bundle2.putString("group_id",getIntent().getStringExtra("group_id"));
        bundle2.putString("team_id",getIntent().getStringExtra("team_id"));
        bundle2.putString("type","OUT");
        fragments[1].setArguments(bundle2);
        AttendancePagerAdapter adapter=new AttendancePagerAdapter(getSupportFragmentManager(),fragments,new String[]{"IN","OUT"});
        viewPager.setAdapter(adapter);
    }
}
