package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.committee.committeeResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.BoothStudentListFragment;

public class BoothStudentActivity extends BaseActivity {

    public static final String TAG = "BoothStudentActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    private String mGroupId;
    private String teamId;
    private String committeeID;
    MyTeamData classData;
    committeeResponse.committeeData committeeData;
    private BoothStudentListFragment classListFragment;

    private static final int ACTIVITY_REQUEST_CODE = 0;


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

        classListFragment = new BoothStudentListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, classListFragment).commit();

    }

    private void init() {
        if (getIntent().getExtras() != null) {
            classData = new Gson().fromJson(getIntent().getStringExtra("class_data"), MyTeamData.class);
            committeeData = new Gson().fromJson(getIntent().getStringExtra("committee_data"), committeeResponse.committeeData.class);
            Log.e(TAG,"committee Data: "+new Gson().toJson(committeeData));
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
        getMenuInflater().inflate(R.menu.menu_member, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_add_member: {
                Intent intent = new Intent(this, AddBoothStudentActivity.class);
                intent.putExtra("group_id", mGroupId);
                intent.putExtra("team_id", teamId);
                intent.putExtra("category", classData.category);
                intent.putExtra("committee_data", new Gson().toJson(committeeData));
                intent.putExtra("mobileList", classListFragment.getMobileList());
                startActivity(intent);
                return true;
            }

            case R.id.edit_committee: {

                Intent intent = new Intent(getApplicationContext(), AddCommiteeActivity.class);
                intent.putExtra("screen","update");
                intent.putExtra("team_id",teamId);
                intent.putExtra("committee_id",committeeData.getCommitteeId());
                intent.putExtra("committee_name",committeeData.getCommitteeName());
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE);

                return true;
            }


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String returnString = data.getStringExtra("Delete");

                if (returnString.equalsIgnoreCase("Yes"))
                {
                   finish();
                }
                else if (returnString.equalsIgnoreCase("No"))
                {
                    setTitle(data.getStringExtra("Title")+" Members");
                }

            }
        }
    }
}
