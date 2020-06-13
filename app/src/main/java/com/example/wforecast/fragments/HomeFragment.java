package com.example.wforecast.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.wforecast.R;
import com.example.wforecast.viewmodels.HomeViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = root.findViewById(R.id.tabs);
        viewPager = root.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(tabLayout.getTabCount(), getChildFragmentManager(), getActivity());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private int tabs;
        private Context mContext;
        private List<String> tabTitles;

        public SectionsPagerAdapter(int tabs, FragmentManager fm, Context context) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.tabs = tabs;
            mContext = context;
            tabTitles = new ArrayList<>(tabs);
            tabTitles.add(mContext.getString(R.string.CurrentLocation));
            tabTitles.add(mContext.getString(R.string.Forecast));
            tabTitles.add(mContext.getString(R.string.Settings));
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CurrentLocationFragment.newInstance();
                case 1:
                    return FavoritesFragment.newInstance();
                case 2:
                    return SearchFragment.newInstance();
                default:
                    return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }

        @Override
        public int getCount() {
            return tabs;
        }
    }
}
