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
import school.campusconnect.fragments.EBookPdfTeamFragment;

public class EBookPdfForTeamActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;
    String team_id;
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);

        if(getIntent().getExtras()!=null){
            setTitle(getIntent().getStringExtra("title"));
            role = getIntent().getStringExtra("role");
            team_id = getIntent().getStringExtra("team_id");
        }

        EBookPdfTeamFragment classListFragment=new EBookPdfTeamFragment();
        classListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if("admin".equalsIgnoreCase(role))
            getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAdd) {
            Intent intent = new Intent(this, AddEBookActivity2.class);
            intent.putExtra("group_id", GroupDashboardActivityNew.groupId);
            intent.putExtra("team_id", team_id);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
