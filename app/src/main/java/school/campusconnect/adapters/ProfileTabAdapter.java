package school.campusconnect.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import school.campusconnect.utils.AppLog;
import android.view.ViewGroup;

import school.campusconnect.fragments.ProfileBasicFragment;
import school.campusconnect.fragments.ProfileOtherFragment;


public class ProfileTabAdapter extends FragmentPagerAdapter {
    String[] titles = {"gruppie", "all"};
    Fragment f, f1;

    public ProfileTabAdapter(FragmentManager fm, ProfileBasicFragment f, ProfileOtherFragment f1) {
        super(fm);
        this.f = f;
        this.f1 = f1;
    }

    @Override
    public Fragment getItem(int position) {
       AppLog.e("getItem", "getItem: position " + position);
        switch (position) {
            case 0: {
                return f;
            }
            case 1: {
                return f1;
            }
            default:
                return f;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return 2;
    }

}
