package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.TeamDiscussFragment;
import school.campusconnect.fragments.TeamPostsFragmentNew;

public class TeamListActivity extends BaseActivity {
    private static final String TAG = TeamListActivity.class.getSimpleName();
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);

        setTitle(getResources().getString(R.string.lbl_view_discuss));

        setBackEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TeamDiscussFragment()).commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void teamPost(MyTeamData myTeamData) {
        myTeamData.groupId = GroupDashboardActivityNew.groupId;
        setTitle(myTeamData.name);
        TeamPostsFragmentNew fragTeamPost = TeamPostsFragmentNew.newInstance(myTeamData, false,"no","no");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragTeamPost).addToBackStack("nested_team").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setTitle(getResources().getString(R.string.lbl_view_discuss));
    }


}
