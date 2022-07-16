package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.school.AddStaffV2Activity;
import school.campusconnect.fragments.ClassStudentListFragment;
import school.campusconnect.fragments.StaffListFragment;

public class StaffActivity extends BaseActivity {

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
        setTitle(getResources().getString(R.string.lbl_staff_register));


        StaffListFragment classListFragment=new StaffListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_staff,menu);
        menu.findItem(R.id.menu_add_staff).setVisible(false);
        menu.findItem(R.id.menu_print_staff_list).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_staff:
                Intent intent = new Intent(this, AddStaffV2Activity.class);
                intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
                startActivity(intent);
                return true;
            case R.id.menu_search:
                ((StaffListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).showHideSearch();
                return true;
            case R.id.menu_print_staff_list:
                ((StaffListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container)).exportDataToCSV();
                return true;
             default:
                 return super.onOptionsItemSelected(item);
        }
    }
}
