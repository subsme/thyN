package com.thyn.task;

import android.support.v4.app.Fragment;

import com.thyn.SingleFragmentActivity;
import com.thyn.tasklist.TaskListFragment;

/**
 * Created by shalu on 8/23/16.
 */
public class RandomTaskActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() { return new RandomTaskFragment();}

}