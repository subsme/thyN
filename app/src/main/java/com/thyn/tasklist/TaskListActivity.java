package com.thyn.tasklist;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import com.thyn.R;
import com.thyn.SingleFragmentActivity;

/**
 * Created by shalu on 2/22/16.
 */
public class TaskListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() { return new TaskListFragment();}

}
