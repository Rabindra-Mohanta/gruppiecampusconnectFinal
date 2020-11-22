package school.campusconnect.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;

import com.readystatesoftware.viewbadger.BadgeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.NotificationsTabAdapter;
import school.campusconnect.network.LeafManager;

/**
 * Created by frenzin04 on 2/28/2017.
 */

public class NotificationsActivityNew extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.tabViewPager)
    ViewPager mPagerView;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    NotificationsTabAdapter tabAdapter;
    int tabPosition;

    LeafManager manager;

    BadgeView badgeGroup, badgeTeam, badgePer;

    public static boolean toFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_new);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getString(R.string.action_notifications));

        toFinish = false;

        invalidateOptionsMenu();
        /*LeafManager manager = new LeafManager();
        manager.readGroupRequest(this, 88, 259);*/

        boolean callApi;

        SharedPreferences pref = this.getSharedPreferences("pref_noti_count", MODE_PRIVATE);
        callApi = pref.getInt("noti_count", 0) != 0;
//        pref.edit().clear().commit();
        pref.edit().putInt("noti_count", 0).commit();
        updateHotCount(0);

        manager = new LeafManager();

        tabAdapter = new NotificationsTabAdapter(getSupportFragmentManager(), 0, callApi);

        mTabLayout.addTab(mTabLayout.newTab().setText("Group"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Team"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Individual"));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPagerView.setCurrentItem(tab.getPosition());
                tabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


//        manager.getCounter(this, mGroupItem.getGroupId());

        mPagerView = (ViewPager) findViewById(R.id.tabViewPager);
        //mPagerView.setAdapter(new NotificationsTabAdapter(getSupportFragmentManager(), mGroupItem.getId()));
        mPagerView.setAdapter(tabAdapter);
        mPagerView.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mPagerView.setOffscreenPageLimit(3);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toFinish) {
            toFinish = false;
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = (FragmentPagerAdapter) mPagerView.getAdapter();
        return getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + mPagerView.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public void nullGroupBadge() {
        badgeGroup = null;
    }

    public void nullTeamBadge() {
        badgeTeam = null;
    }

    public void nullPerBadge() {
        badgePer = null;
    }

    public void showGroupCounter(String num) {
//       AppLog.e("BADGEE", "group num is " + num);
        if (badgeGroup == null) {
           AppLog.e("BADGEE if", "group num is " + num);
            View target = findViewById(R.id.tvGroupCounter);
            badgeGroup = new BadgeView(NotificationsActivityNew.this, target);
            badgeGroup.setText(num);
            badgeGroup.setTextSize(10);
            badgeGroup.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badgeGroup.setBadgeBackgroundColor(Color.BLACK);

            if (!num.equals("0")) {
                badgeGroup.show();
            }
        } else {
           AppLog.e("BADGEE else", "group num is " + num);
            if (!num.equals("0")) {
                badgeGroup.decrement(1);
            } else {
                badgeGroup.hide();
            }
        }
    }

    public void showTeamCounter(String num) {
        if (badgeTeam == null) {
           AppLog.e("BADGEE if", "group num is " + num);
            View target = findViewById(R.id.tvTeamCounter);
            badgeTeam = new BadgeView(NotificationsActivityNew.this, target);
            badgeTeam.setText(num);
            badgeTeam.setTextSize(10);
            badgeTeam.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badgeTeam.setBadgeBackgroundColor(Color.BLACK);
            if (!num.equals("0")) {
                badgeTeam.show();
            }
        } else {
           AppLog.e("BADGEE else", "group num is " + num);
            if (!num.equals("0")) {
                badgeTeam.decrement(1);
            } else {
                badgeTeam.hide();
            }
        }
    }

    public void showPersonalCounter(String num) {
        if (badgePer == null) {
           AppLog.e("BADGEE if", "group num is " + num);
            View target = findViewById(R.id.tvPersonalCounter);
            badgePer = new BadgeView(NotificationsActivityNew.this, target);
            badgePer.setText(num);
            badgePer.setTextSize(10);
            badgePer.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badgePer.setBadgeBackgroundColor(Color.BLACK);
            if (!num.equals("0")) {
                badgePer.show();
            }
        } else {
           AppLog.e("BADGEE else", "group num is " + num);
            if (!num.equals("0")) {
                badgePer.decrement(1);
            } else {
                badgePer.hide();
            }
        }
    }

}
