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
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.fragments.ClassStudentListFragment;
import school.campusconnect.fragments.FeesListFragment;

public class FeesListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    private String mGroupId;
    private String teamId;
    ClassResponse.ClassData classData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        init();

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        if (getIntent().getExtras() != null) {
            setTitle(getIntent().getStringExtra("title"));
        }

        FeesListFragment classListFragment = new FeesListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, classListFragment).commit();

    }

    private void init() {
        if (getIntent().getExtras() != null) {
            classData = new Gson().fromJson(getIntent().getStringExtra("class_data"), ClassResponse.ClassData.class);
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = classData.getId();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fees, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAdd) {
//            Intent intent = new Intent(this, AddClassStudentActivity.class);
//            intent.putExtra("group_id", mGroupId);
//            intent.putExtra("team_id", teamId);
//            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
