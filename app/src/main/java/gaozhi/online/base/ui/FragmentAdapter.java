package gaozhi.online.base.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {
    private BaseFragment[] baseFragment;
    public FragmentAdapter(@NonNull FragmentManager fm, BaseFragment[] baseFragment) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.baseFragment=baseFragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return baseFragment[position];
    }

    @Override
    public int getCount() {
        return baseFragment.length;
    }
}