package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.profile.ProfileTBL;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv2;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv3;
import school.campusconnect.fragments.FamilyListFragment;
import school.campusconnect.fragments.ProfileFragmentConst;
import school.campusconnect.fragments.TestOfflineListFragment;
import school.campusconnect.fragments.TestSubjectListFragment;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.MixOperations;

public class ProfileConstituencyActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.tabLayout)
    public TabLayout tabLayout;
    private Menu menu;

    FamilyListFragment familyListFragment;
    ProfileFragmentConst profileFragmentConst;

    public static final String TAG = "ProfileConstituencyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_const);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle("");

        tabLayout.setVisibility(View.VISIBLE);

        profileFragmentConst = new ProfileFragmentConst();
        profileFragmentConst.setArguments(getIntent().getExtras());

        familyListFragment = new FamilyListFragment();
        familyListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragmentConst).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    if (menu != null) {
                        menu.findItem(R.id.menu_add_class_student).setVisible(false);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFragmentConst).commit();
                } else {

                    if (menu != null) {
                        menu.findItem(R.id.menu_add_class_student).setVisible(true);
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, familyListFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        AppLog.e(TAG,"currFrag"+currFrag);

        if (currFrag instanceof ProfileFragmentConst)
        {

        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_family_member, menu);
        this.menu = menu;
        menu.findItem(R.id.menu_add_class_student).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_class_student) {
            if (familyListFragment != null) {
                Intent intent = new Intent(this, AddFamilyStudentActivity.class);
                intent.putExtra("data",new Gson().toJson(familyListFragment.getList()));
                intent.putExtra("pos",-1);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void callUpdateApi() {
        if(profileFragmentConst!=null){
            profileFragmentConst.callUpdateProfileApi();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
