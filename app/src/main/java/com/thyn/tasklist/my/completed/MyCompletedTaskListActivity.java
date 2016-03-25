package com.thyn.tasklist.my.completed;

import android.support.v4.app.Fragment;

import com.thyn.SingleFragmentActivity;

/**
 * Created by shalu on 2/22/16.
 */
public class MyCompletedTaskListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {

        return new MyCompletedTaskListFragment();}

}
