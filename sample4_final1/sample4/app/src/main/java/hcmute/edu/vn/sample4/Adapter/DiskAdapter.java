package hcmute.edu.vn.sample4.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class DiskAdapter extends FragmentPagerAdapter {

    public Fragment fragments ;

    public DiskAdapter(@NonNull FragmentManager fm) {
        super(fm);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments;
    }


    @Override
    public int getCount() {
        return 0;
    }
}
