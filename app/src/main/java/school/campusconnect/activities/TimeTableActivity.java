package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.TimeTableFragment;
import school.campusconnect.network.LeafManager;

public class TimeTableActivity extends BaseActivity {


    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    private LeafManager leafManager;
    private String role="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
         role= getIntent().getStringExtra("role");
        init();
        TimeTableFragment timeTableFragment=new TimeTableFragment();
        timeTableFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,timeTableFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if("teacher".equals(role) || "admin".equals(role)){
            getMenuInflater().inflate(R.menu.menu_time_table,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            default:
                return super.onOptionsItemSelected(item);
            case R.id.menu_add_time_table:
                startActivity(new Intent(this,TimeTableTeamsActivity.class));
                return true;
        }

    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_time_table));
        leafManager = new LeafManager();


    }

}
