package photogallery.android.bignerdranch.com.thyn.tab;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import photogallery.android.bignerdranch.com.thyn.R;
import photogallery.android.bignerdranch.com.thyn.tab.view.SlidingTabLayout;

/**
 * Created by shalu on 3/11/16.
 */
public class MyTaskListActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private SlidingTabsBasicFragment.SamplePagerAdapter mSamplePagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;

    private static String LOG_TAG="MyTaskListActivity";
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytask_slidingtab);
        if (savedInstanceState == null) {
            mViewPager = (ViewPager)findViewById(R.id.viewpager);
            mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            mSamplePagerAdapter = fragment.new SamplePagerAdapter(getSupportFragmentManager());
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            mViewPager.setAdapter(mSamplePagerAdapter);

            mSlidingTabLayout.setDistributeEvenly(true);
            mSlidingTabLayout.setViewPager(mViewPager);

            //transaction.replace(R.id.sample_content_fragment, fragment);
           // transaction.commit();
            /*Log.d(LOG_TAG, "TAb position is: " + TAB_POSITION);
            mViewPager.setCurrentItem(TAB_POSITION);*/



        }




    }


}
