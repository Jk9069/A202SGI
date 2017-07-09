package com.example.user.pickeat4me;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 5/20/2017.
 */

public class nearMeFragment extends Fragment {

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance){
        View view = inflater.inflate(R.layout.near_me_fragment, container, false);
        TabLayout tabHost = (TabLayout) view.findViewById(R.id.nearMe_tabLayout);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.nearMe_viewPager);

        initTabs(tabHost, viewPager);
        return view;
    }

    public void initTabs (TabLayout tabhost, final ViewPager viewPager){
        final String BAR_TAB = "BAR";
        final String CAFE_TAB = "CAFE";
        final String RES_TAB = "RESTAURANT";

        tabhost.addTab(tabhost.newTab().setText(BAR_TAB));
        tabhost.addTab(tabhost.newTab().setText(CAFE_TAB));
        tabhost.addTab(tabhost.newTab().setText(RES_TAB));

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabhost.getTabCount()));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabhost));
        tabhost.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //class to initialise fragments into pages as an adapter that the tab layout can read
    private class PagerAdapter extends FragmentStatePagerAdapter {
        int noOfTabs;

        private PagerAdapter(FragmentManager fm, int numTabs){
            super (fm);
            noOfTabs = numTabs;
        }

        @Override
        public Fragment getItem (int position){
            Fragment f;
            switch (position){
                case 0:
                    f = new bar_tab();
                    break;
                case 1:
                    f = new cafe_tab();
                    break;
                case 2:
                    f = new res_tab();
                    break;
                default:
                    return null;
            }
            return f;
        }

        @Override
        public int getCount(){
            return noOfTabs;
        }
    }
}
