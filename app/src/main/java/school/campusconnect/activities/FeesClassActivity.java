package school.campusconnect.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.ClassListFragment2;
import school.campusconnect.fragments.FeesClassListFragment;
import school.campusconnect.fragments.PaidFeesFragment;

public class FeesClassActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tv_toolbar_title)
    public TextView tvTitle;

    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;

    PaidFeesFragment paidFeesFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fees_payment);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getIntent().getStringExtra("title"));

        String role  = getIntent().getStringExtra("role");

        if("admin".equalsIgnoreCase(role)){
            tabLayout.setVisibility(View.VISIBLE);
            FeesClassListFragment classListFragment=new FeesClassListFragment();
            classListFragment.setArguments(getIntent().getExtras());

            paidFeesFragment =new PaidFeesFragment();
            paidFeesFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,paidFeesFragment).commit();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if(tab.getPosition()==0){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,paidFeesFragment).commit();
                    }else{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }else {
            tabLayout.setVisibility(View.GONE);
            FeesClassListFragment classListFragment=new FeesClassListFragment();
            classListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,classListFragment).commit();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(paidFeesFragment!=null && tabLayout.getSelectedTabPosition()==0){
            paidFeesFragment.callApi();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
