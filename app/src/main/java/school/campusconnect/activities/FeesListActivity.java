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
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.fragments.ClassStudentListFragment;
import school.campusconnect.fragments.FeesListFragment;

public class FeesListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    private String mGroupId;
    private String teamId;
    private String userId;
    private String role;
    private boolean accountant;
    private Menu menu;
    private FeesListFragment classListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        init();

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        if (getIntent().getExtras() != null) {
            setTitle(getIntent().getStringExtra("title"));
        }

        classListFragment = new FeesListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, classListFragment).commit();

    }

    private void init() {
        if (getIntent().getExtras() != null) {
            mGroupId = GroupDashboardActivityNew.groupId;
            teamId = getIntent().getStringExtra("team_id");
            userId = getIntent().getStringExtra("user_id");
            role = getIntent().getStringExtra("role");
            accountant = getIntent().getBooleanExtra("accountant",false);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if("admin".equalsIgnoreCase(role)){
            getMenuInflater().inflate(R.menu.menu_fees, menu);
        }
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAdd) {
            Intent intent = new Intent(this, AddFeesActivity.class);
            intent.putExtra("group_id", mGroupId);
            intent.putExtra("team_id", teamId);
            if(menu!=null){
                intent.putExtra("title", menu.findItem(R.id.menuAdd).getTitle()+" - "+getIntent().getStringExtra("title"));
            }else {
                intent.putExtra("title", "Add/Update Fees");
            }

            startActivity(intent);
            return true;
        }
        if(item.getItemId() == R.id.menuPrintFees){
            if(classListFragment!=null){
                classListFragment.exportDataToCSV();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setOptionMenuName(String menuName) {
        if(menu!=null){
            if(menu.findItem(R.id.menuAdd)!=null){
                menu.findItem(R.id.menuAdd).setTitle(menuName);
            }
        }
    }
}
