package school.campusconnect.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AttendancePagerAdapter extends FragmentPagerAdapter {
    Fragment[] fragments;
    String[] titles;
    public AttendancePagerAdapter(FragmentManager fm, Fragment[] fragments, String[] titles) {
        super(fm);
        this.fragments=fragments;
        this.titles=titles;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
