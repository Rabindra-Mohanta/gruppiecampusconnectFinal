package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.TimeTableTeamFragment;

public class TimeTableTeamsActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_teams);


        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        setTitle(getResources().getString(R.string.lbl_add_time_table_for));

        TimeTableTeamFragment timeTableTeamFragment=new TimeTableTeamFragment();
        timeTableTeamFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,timeTableTeamFragment).commit();

    }
}
