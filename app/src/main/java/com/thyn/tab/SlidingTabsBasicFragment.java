package com.thyn.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.TextView;

import android.util.Log;


import com.thyn.tasklist.TaskListFragment;
import com.thyn.tab.view.SlidingTabLayout;
import com.thyn.tasklist.my.MyTaskListFragment;
import com.thyn.tasklist.my.completed.MyCompletedTaskListFragment;
import com.thyn.R;

import org.w3c.dom.Text;
import android.widget.ImageView;
public class SlidingTabsBasicFragment extends Fragment {

private static String LOG_TAG= "SlidingTabsBasicFragment";


    //private SlidingTabLayout mSlidingTabLayout;

    /*
    * A {@link Viewpager} which will be used with SlidingTabLayout
     */
   // private ViewPager mViewPager;

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasklist, container, false);
        return v;
    }*/
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    //@Override
    //public void onViewCreated(View view, Bundle savedInstanceState) {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
       /* mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);*/

    //}

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends FragmentPagerAdapter {


        public SamplePagerAdapter(FragmentManager fm){
            super(fm);
        }
        /*public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }*/
        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 2;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        /*@Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }*/

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence cs = null;

            switch(position){
                case 0: cs = "GIVE HELP" ;break;
                default: cs = "GET HELP";break;

            }
            //return "Item " + (position + 1);
            return cs;
        }

        @Override
        public Fragment getItem(int i) {
            Log.d(LOG_TAG, "In getItem(i)" + i);
            Log.d(LOG_TAG, "Position clicked is: " + i);

            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    //return new TaskListFragment();
                    TaskListFragment tlf = TaskListFragment.newInstance(false);
                    return tlf;
                default:
                    MyTaskListFragment m = MyTaskListFragment.newInstance(false);
                    return m;

            }
        }

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
     /*   @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            // Retrieve a TextView from the inflated View, and update it's text
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText(String.valueOf(position + 1));

            Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");

            // Return the View
            return view;
        }*/

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
     /*   @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }*/

    }

}