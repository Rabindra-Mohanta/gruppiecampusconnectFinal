package school.campusconnect.activities;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.CommitteeListFragment;
import school.campusconnect.utils.AppLog;

public class CommitteeActivity extends BaseActivity {

    private static final String TAG = "CommitteeActivity";

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    String TeamID;
    private MyTeamData classData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_committee));

        if (getIntent() != null)
        {
            classData = new Gson().fromJson(getIntent().getStringExtra("class_data"), MyTeamData.class);
            AppLog.e(TAG, "classData : " + classData);
            TeamID = classData.teamId;
        }

        CommitteeListFragment committeeListFragment=new CommitteeListFragment();
        committeeListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,committeeListFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_committee,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_committee:
                Intent intent = new Intent(getApplicationContext(), AddCommiteeActivity.class);
                intent.putExtra("screen","add");
                intent.putExtra("team_id",TeamID);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}