package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.booths.BoothData;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.BoothStudentListFragment;
import school.campusconnect.fragments.ClassStudentListFragment;

public class BoothStudentActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    private String mGroupId;
    private String teamId;
    MyTeamData classData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        init();

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        if (getIntent().getExtras() != null) {
            setTitle(getIntent().getStringExtra("title")+" Members");
        }

        BoothStudentListFragment classListFragment = new BoothStudentListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, classListFragment).commit();

    }

    private void init() {
        if (getIntent().getExtras() != null) {
            classData = new Gson().fromJson(getIntent().getStringExtra("class_data"), MyTeamData.class);
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = classData.teamId;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_booth_student, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_class_student: {
                Intent intent = new Intent(this, AddBoothStudentActivity.class);
                intent.putExtra("group_id", mGroupId);
                intent.putExtra("team_id", teamId);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
