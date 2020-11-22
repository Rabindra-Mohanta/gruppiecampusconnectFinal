package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.NestedTeamFragment;
import school.campusconnect.fragments.TeamPostsFragmentNew;

public class NestedTeamActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
/*
    @Bind(R.id.lin_toolbar)
    public RelativeLayout lin_toolbar;*/

    @Bind(R.id.tv_toolbar_title_dashboard)
    public TextView tvTitle;

    @Bind(R.id.tv_Desc)
    public TextView tv_Desc;
    private String groupId;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_team);

        groupId=getIntent().getStringExtra("group_id");
        title=getIntent().getStringExtra("title")+" Teams";

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        setTitle(title);

        NestedTeamFragment nestedTeamFragment=new NestedTeamFragment();
        nestedTeamFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,nestedTeamFragment).commit();
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

    public void setTeamPostFragment(MyTeamData myTeamData)
    {
        myTeamData.groupId=groupId;
        setTitle(myTeamData.name);
        TeamPostsFragmentNew fragTeamPost = TeamPostsFragmentNew.newInstance(myTeamData,false);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragTeamPost).addToBackStack("nested_team").commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setTitle(title);
    }
}
