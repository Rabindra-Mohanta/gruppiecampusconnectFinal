package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.FeesClassListFragment;
import school.campusconnect.fragments.PaidFeesFragment;
import school.campusconnect.fragments.TestOfflineListFragment;
import school.campusconnect.fragments.TestSubjectListFragment;

public class TestClassSubjectActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.iconBack)
    public ImageView iconBack;

    @Bind(R.id.imgHome)
    public ImageView imgHome;

    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;

    String role;
    String team_id;
    String title;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_class);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(false);
        tvTitle.setText(getIntent().getStringExtra("title"));
        setTitle("");

        title = getIntent().getStringExtra("title");
        role = getIntent().getStringExtra("role");
        team_id = getIntent().getStringExtra("team_id");

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestClassSubjectActivity.this, GroupDashboardActivityNew.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        tabLayout.setVisibility(View.VISIBLE);
        TestOfflineListFragment testOfflineListFragment = new TestOfflineListFragment();
        testOfflineListFragment.setArguments(getIntent().getExtras());

        TestSubjectListFragment testSubjectFragmement = new TestSubjectListFragment();
        testSubjectFragmement.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, testOfflineListFragment).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    if (menu != null) {
                        menu.findItem(R.id.menu_schedule_exam).setVisible(true);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, testOfflineListFragment).commit();
                } else {

                    if (menu != null) {
                        menu.findItem(R.id.menu_schedule_exam).setVisible(false);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, testSubjectFragmement).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ("admin".equalsIgnoreCase(role) || "teacher".equalsIgnoreCase(role)) {
            getMenuInflater().inflate(R.menu.menu_test_offline, menu);
            this.menu = menu;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_schedule_exam) {
            Intent intent = new Intent(this, AddOfflineTestActivity.class);
            intent.putExtra("groupId", GroupDashboardActivityNew.groupId);
            intent.putExtra("teamId", team_id);
            intent.putExtra("title", title);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
