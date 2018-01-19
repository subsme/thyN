package com.thyn.task.view;

import android.support.v4.app.Fragment;


import com.thyn.SingleFragmentActivity;

/**
 * Created by shalu on 2/23/16.
 */
public class TaskPagerViewOnlyActivity extends SingleFragmentActivity {


    private static String TAG = "TaskPagerViewOnlyActivity";

    @Override
    protected Fragment createFragment() {
        Long taskID = (Long)getIntent()
                .getSerializableExtra(TaskPagerViewOnlyFragment.EXTRA_TASK_ID);
        return TaskPagerViewOnlyFragment.newInstance(taskID);

    }
}
