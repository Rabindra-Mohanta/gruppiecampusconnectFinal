package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.HWListFragment;
import school.campusconnect.fragments.HWSubjectListFragment;

public class HWListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    boolean canPost;
    String team_id;
    String subject_id;
    String subject_name;
    String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        className = getIntent().getStringExtra("className");
        setTitle(getIntent().getStringExtra("title")+" - ("+className+")");

        canPost = getIntent().getBooleanExtra("canPost",false);
        team_id = getIntent().getStringExtra("team_id");
        subject_id = getIntent().getStringExtra("subject_id");
        subject_name = getIntent().getStringExtra("subject_name");

        HWListFragment classListFragment=new HWListFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (canPost) {
            getMenuInflater().inflate(R.menu.menu_add,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAdd:
                Intent intent = new Intent(this,AddHwPostActivity.class);
                intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
                intent.putExtra("team_id",team_id);
                intent.putExtra("subject_id",subject_id);
                intent.putExtra("subject_name",subject_name);
                intent.putExtra("className",className);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
