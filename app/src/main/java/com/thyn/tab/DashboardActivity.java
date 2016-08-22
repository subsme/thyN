package com.thyn.tab;

import android.os.Bundle;


import com.thyn.tab.view.SlidingTabLayout;
import com.thyn.tasklist.TaskListFragment;
import com.thyn.tasklist.my.MyTaskListFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.thyn.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    public static final String SELECT_SECOND_TAB =
            "com.android.android.thyn.tab.dashborad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int tab = 0;
        Bundle args = getIntent().getExtras();
        if(args != null) {
            Log.d(TAG, "Second tab selected");
            boolean id = args.getBoolean(SELECT_SECOND_TAB, false);
            if (id) {
                tab = 1;
            }
        }

        setContentView(R.layout.activity_dashboard);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle("Hello world App");  // if i want to change the title.

        if (savedInstanceState == null) {
            mViewPager = (ViewPager)findViewById(R.id.viewpager);
            mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            DashboardPagerAdapter dashboardPagerAdapter = new DashboardPagerAdapter(getSupportFragmentManager());
            mViewPager.setAdapter(dashboardPagerAdapter);
            mViewPager.setCurrentItem(tab);
            mSlidingTabLayout.setDistributeEvenly(true);

            mSlidingTabLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.bg_screen5));
            mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getBaseContext(), R.color.tab_highlighter));
            mSlidingTabLayout.setViewPager(mViewPager);
        }
        ImageView imageView = new ImageView(this); // Create an icon
        imageView.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.plus_white));

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(imageView)
                .setBackgroundDrawable(R.drawable.selector_button_green)
                .build();




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();



        return super.onOptionsItemSelected(item);
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
        switch (position) {
            case 0:
                TaskListFragment tlf = TaskListFragment.newInstance(true);
                return tlf;
            default:
                MyTaskListFragment m = MyTaskListFragment.newInstance(true);
                return m;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
}
