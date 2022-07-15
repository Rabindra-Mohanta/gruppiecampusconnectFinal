package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityMasterListBinding;
import school.campusconnect.databinding.ActivityWorkerListBinding;
import school.campusconnect.fragments.MasterListFragment;
import school.campusconnect.fragments.WorkerListFragment;

public class WorkerListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    ActivityWorkerListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_worker_list);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("name"));
        inits();
    }

    private void inits() {


        WorkerListFragment workerListFragment = new WorkerListFragment();
        workerListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,workerListFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}