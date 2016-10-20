package com.thyn.tab;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;


import com.squareup.picasso.Picasso;
import com.thyn.common.MyServerSettings;
import com.thyn.graphics.MLRoundedImageView;
import com.thyn.tab.view.SlidingTabLayout;
import com.thyn.R;

import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import com.thyn.graphics.SquareImageGridAdapter;
import com.thyn.tasklist.my.MyTaskListFragment;

import android.view.Window;
/**
 * Created by shalu on 3/11/16.
 */
public class WelcomePageActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private SlidingTabsBasicFragment.SamplePagerAdapter mSamplePagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;


    private static String TAG="WelcomePageActivity";
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mytask_slidingtab);
        Context c = getApplicationContext();
        if (savedInstanceState == null) {
            TextView welcomeText = (TextView) findViewById(R.id.title_text);
            welcomeText.setText("Welcome, " + MyServerSettings.getUserFirstName(c) + "!");
            MLRoundedImageView user_profile_image = (MLRoundedImageView) findViewById(R.id.user_profile_image);
            Log.d(TAG, MyServerSettings.getUserProfileImageURL(c));
            Picasso.with(c)
                    // .load("https://scontent.xx.fbcdn.net/v/t1.0-1/p50x50/11156187_10205188530126207_4481467444246362898_n.jpg?oh=2dee76ec7e202649b84c7a71b4c86721&oe=58ADEBE1")
                    .load(MyServerSettings.getUserProfileImageURL(c))
                    .into(user_profile_image);
            GridView gridView = (GridView) findViewById(R.id.gridview);
            SquareImageGridAdapter sg = new SquareImageGridAdapter(this);
            sg.setGrid1(MyServerSettings.getNumNeighbrsIHelped(c) + "", "Neighbrs I have helped");
            sg.setGrid2(MyServerSettings.getPoints(c) + "", "thyNeighbr points");
            // sg.setGrid1("50", "Open requests(20 miles)");
            // sg.setGrid2("0", "Requests in your queue");
            // sg.setGrid3(MyServerSettings.getNumNeighbrsIHelped(c)+"", "Neighbrs helped");
            // sg.setGrid4(MyServerSettings.getPoints(c)+"", "thyNeighbr points");
            gridView.setAdapter(sg);
            mViewPager = (ViewPager) findViewById(R.id.viewpager);
            mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            mSamplePagerAdapter = fragment.new SamplePagerAdapter(getSupportFragmentManager());
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            mViewPager.setAdapter(mSamplePagerAdapter);

            mSlidingTabLayout.setDistributeEvenly(true);
            mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return ContextCompat.getColor(getBaseContext(), R.color.tab_highlighter);
                }
            });
            mSlidingTabLayout.setViewPager(mViewPager);


            //transaction.replace(R.id.sample_content_fragment, fragment);
            // transaction.commit();
            /*Log.d(LOG_TAG, "TAb position is: " + TAB_POSITION);
            mViewPager.setCurrentItem(TAB_POSITION);*/
        }
    }


}
