package cn.edu.hfut.lilei.shareboard.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hfut.lilei.shareboard.fragment.MeetingFragment;

public class TabPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private List<String> tagList = new ArrayList<>();
    private FragmentManager fm;

    public TabPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    public Object instantiateItem(ViewGroup container, int position) {
        tagList.add(makeFragmentName(container.getId(), getItemId(position)));
        return super.instantiateItem(container, position);
    }

    public static String makeFragmentName(int viewId, long index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    public void update(int item) {
        Fragment fragment = fm.findFragmentByTag(tagList.get(item));
        if (fragment != null) {
            switch (item) {
                case 0:
                    ((MeetingFragment) fragment).update();
                    break;
                case 1:

                    break;
                case 2:

                    break;
                default:
                    break;
            }
        }
    }


}
