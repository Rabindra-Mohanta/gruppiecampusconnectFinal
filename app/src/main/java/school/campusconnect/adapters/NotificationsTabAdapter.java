package school.campusconnect.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import school.campusconnect.fragments.NotificationsFragment;


public class NotificationsTabAdapter extends FragmentPagerAdapter {
    int mGroupId;
    String[] titles = {"Posts", "Team", "Me"};
    Fragment f, f1, f2;
    boolean callApi;

    public NotificationsTabAdapter(FragmentManager fm, int groupId, boolean callApi) {
        super(fm);
        this.mGroupId = groupId;
        this.callApi = callApi;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                f = NotificationsFragment.newInstance(1, callApi);
                return f;
            }
            case 1: {
                f1 = NotificationsFragment.newInstance(2, callApi);
                return f1;
            }
            case 2: {
                f2 = NotificationsFragment.newInstance(3, callApi);
                return f2;
            }
            default:
                return NotificationsFragment.newInstance(1, callApi);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return 3;
    }


}
