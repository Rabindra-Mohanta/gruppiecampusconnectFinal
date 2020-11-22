package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.AddTeamClassListFragment;
import school.campusconnect.fragments.AddTeamStudentListFragment;

public class AddTeamStudentActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_students));


        AddTeamClassListFragment classListFragment=new AddTeamClassListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();

    }
    public void next(Bundle bundle){
        AddTeamStudentListFragment classListFragment=new AddTeamStudentListFragment();
        classListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).addToBackStack("aa").commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
