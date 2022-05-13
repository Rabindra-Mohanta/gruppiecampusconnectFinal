package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityMasterListBinding;
import school.campusconnect.fragments.MasterListFragment;

public class MasterListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    ActivityMasterListBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_master_list);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("name"));
        inits();
    }

    private void inits() {

        MasterListFragment masterListFragment = new MasterListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,masterListFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}