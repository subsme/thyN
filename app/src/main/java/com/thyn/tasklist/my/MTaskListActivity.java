package com.thyn.tasklist.my;

import android.support.v4.app.Fragment;

import com.thyn.SingleFragmentActivity;

/**
 * Created by shalu on 2/22/16.
 */
public class MTaskListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {

        return new MyTaskListFragment();}

}
