package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.BoothListFragment2;
import school.campusconnect.fragments.CommitteeListFragment;

public class CommitteeActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_booth_register));


        CommitteeListFragment committeeListFragment=new CommitteeListFragment();
        committeeListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,committeeListFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_booth,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_class:
                //startActivity(new Intent(this,AddBoothActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}