package com.thyn.form.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import java.util.ArrayList;


import com.thyn.collection.Task;
import com.thyn.collection.MyTaskLab;
import com.thyn.R;
/**
 * Created by shalu on 2/23/16.
 */
public class TaskPagerViewOnlyActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private ArrayList<Task> mTasks;
    private static String TAG = "TaskPagerViewOnlyActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mTasks = MyTaskLab.get(this).getTasks();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Task task = mTasks.get(position);
                return TaskPagerViewOnlyFragment.newInstance(task.getId());
            }

            @Override
            public int getCount() {
                return mTasks.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            public void onPageScrollStateChanged(int state){}
            public void onPageScrolled(int pos, float posOffset, int posOffsetPixels){}
            public void onPageSelected(int pos){
                /* to do see page 207 big nerd if you want to change the page title */
            }
        });

        Long taskID = (Long)getIntent()
                .getSerializableExtra(TaskPagerViewOnlyFragment.EXTRA_TASK_ID);
        for(int i=0; i<mTasks.size();i++) {
            //Log.d(TAG,"Task element: " + mTasks.get(i).getId());
            if(mTasks.get(i).getId().equals(taskID)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
