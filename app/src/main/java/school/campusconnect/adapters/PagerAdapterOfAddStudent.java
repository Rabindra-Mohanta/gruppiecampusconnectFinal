package school.campusconnect.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import school.campusconnect.fragments.BasicInfoFragment;
import school.campusconnect.fragments.FamilyInfoFragment;
import school.campusconnect.fragments.OtherInfoFragment;
import school.campusconnect.fragments.ProfileBasicFragment;
import school.campusconnect.fragments.ProfileOtherFragment;
import school.campusconnect.utils.AppLog;

public class PagerAdapterOfAddStudent extends FragmentPagerAdapter {


    public PagerAdapterOfAddStudent(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 1:
                return new OtherInfoFragment();
            case 2:
                return new FamilyInfoFragment();
            default:
                return new BasicInfoFragment();
        }

    }


    @Override
    public int getCount() {
        return 3;
    }
}