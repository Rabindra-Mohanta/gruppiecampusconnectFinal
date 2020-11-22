package school.campusconnect.activities;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.MarkSheetListFragment;

public class MarkSheetListActivity extends BaseActivity {


    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        init();
        MarkSheetListFragment markSheetListFragment=new MarkSheetListFragment();
        markSheetListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,markSheetListFragment).commit();
    }
    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("name")+" Mark Card");
    }
}
