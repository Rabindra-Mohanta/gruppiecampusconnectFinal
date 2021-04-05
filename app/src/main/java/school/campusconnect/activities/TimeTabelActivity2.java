package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import school.campusconnect.R;
import school.campusconnect.fragments.SubjectListFragment2;
import school.campusconnect.fragments.TimeTableListFragment2;
import butterknife.Bind;
import butterknife.ButterKnife;

public class TimeTabelActivity2 extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        role = getIntent().getStringExtra("role");
        setTitle(getIntent().getStringExtra("team_name"));


        TimeTableListFragment2 classListFragment=new TimeTableListFragment2();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if("admin".equals(role)){
            getMenuInflater().inflate(R.menu.menu_class,menu);
            menu.findItem(R.id.menu_add_class).setTitle("Add/Update Time Table");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_class:
                Intent intent = new Intent(this, AddTimeTable2.class);
                intent.putExtra("team_id",getIntent().getStringExtra("team_id"));
                intent.putExtra("team_name",getIntent().getStringExtra("team_name"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
