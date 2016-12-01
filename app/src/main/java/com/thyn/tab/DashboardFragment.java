package com.thyn.tab;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.thyn.R;
import com.thyn.collection.Filter;
import com.thyn.navigate.NavigationActivity;
import com.thyn.tab.view.SlidingTabLayout;
import com.thyn.task.RandomTaskActivity;
import com.thyn.task.RandomTaskFragment;
import com.thyn.tasklist.TaskListFragment;
import com.thyn.tasklist.iwillhelp.IWillHelpTaskListFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFrgment";
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SELECT_SECOND_TAB = "com.thyn.tab.dashboard";
    private static final String REFRESH_NEEDED = "com.thyn.tab.refresh";

    // TODO: Rename and change types of parameters
    private boolean toRefresh;
    private boolean id;



    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param toRefresh Parameter 1.
     * @param selectSecondTab Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(boolean toRefresh, boolean selectSecondTab) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putBoolean(REFRESH_NEEDED, toRefresh);
        args.putBoolean(SELECT_SECOND_TAB, selectSecondTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toRefresh = getArguments().getBoolean(REFRESH_NEEDED);
            id = getArguments().getBoolean(SELECT_SECOND_TAB);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Toolbar myToolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Requests and Errands");

        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout)view.findViewById(R.id.sliding_tabs);

        DashboardPagerAdapter dashboardPagerAdapter = new DashboardPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(dashboardPagerAdapter);
        int tab = 0;
        if(id) tab = 1;
        mViewPager.setCurrentItem(tab);
        mSlidingTabLayout.setDistributeEvenly(true);

        mSlidingTabLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_screen5));
        mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getContext(), R.color.tab_highlighter));
        mSlidingTabLayout.setViewPager(mViewPager);
        ImageView imageView = new ImageView(getActivity()); // Create an icon
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.plus_white));

        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).showFloatingActionButton();
        }

        return view;
    }

    class DashboardPagerAdapter extends FragmentPagerAdapter {
        String[] tabs;

        public DashboardPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.dashboard_tabs);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "Sending refresh value  to TaskListFragment and IWillHelpTaskLisFragment. Refresh value: " + toRefresh);
            switch (position) {
                case 0:

                    TaskListFragment tlf = TaskListFragment.newInstance(true, toRefresh);
                    return tlf;
                default:
                    IWillHelpTaskListFragment i = IWillHelpTaskListFragment.newInstance(toRefresh);
                    return i;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
