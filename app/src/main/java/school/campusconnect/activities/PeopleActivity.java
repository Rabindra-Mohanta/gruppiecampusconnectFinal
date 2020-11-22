package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.PeopleFragment;

public class PeopleActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;
    private boolean isNest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            isNest=bundle.getBoolean("isNest",false);
            setTitle(bundle.getString("title",""));
        }
        if(!isNest)
            setTitle(getResources().getString(R.string.lbl_my_people));


        PeopleFragment peopleFragment=new PeopleFragment();
        peopleFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,peopleFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
