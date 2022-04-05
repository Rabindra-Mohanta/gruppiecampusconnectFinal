package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.ArchiveTeamFragment;
import school.campusconnect.fragments.TeamPostsFragmentNew;

public class ArchiveTeamActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_team);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_archive_team);
        ArchiveTeamFragment archiveTeamFragment=new ArchiveTeamFragment();
        archiveTeamFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,archiveTeamFragment).commit();
    }

    public void setTeamPostFragment(MyTeamData myTeamData)
    {
        setTitle(myTeamData.name);
        TeamPostsFragmentNew fragTeamPost = TeamPostsFragmentNew.newInstance(myTeamData,false,"no","no");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragTeamPost).addToBackStack("archive_team").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setTitle(R.string.lbl_archive_team);
    }
}
