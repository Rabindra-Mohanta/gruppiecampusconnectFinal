package school.campusconnect.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;


public class GroupTabAdapter extends FragmentPagerAdapter {
    String mGroupId;
    private Fragment f, f1;

    public GroupTabAdapter(FragmentManager fm, String groupId, Fragment f, Fragment f1) {
        super(fm);
        this.mGroupId = groupId;
        this.f = f;
        this.f1 = f1;
    }

    @Override
    public Fragment getItem(int position) {
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
