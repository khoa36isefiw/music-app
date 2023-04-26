package hcmute.edu.vn.sample4.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Objects;

import hcmute.edu.vn.sample4.Fragment.GalleryFragment;
import hcmute.edu.vn.sample4.Fragment.HomeFragment;
import hcmute.edu.vn.sample4.Fragment.SearchFragment;

public class ViewPaperAdapter extends FragmentStatePagerAdapter {


    public ViewPaperAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 1:
                return new SearchFragment();
            case 2:
                return new GalleryFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
