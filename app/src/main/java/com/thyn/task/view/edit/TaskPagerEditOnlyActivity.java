package com.thyn.task.view.edit;

import android.support.v4.app.Fragment;

import com.thyn.SingleFragmentActivity;
import com.thyn.task.view.TaskPagerViewOnlyFragment;

/**
 * Created by shalu on 10/25/16.
 */
public class TaskPagerEditOnlyActivity extends SingleFragmentActivity {


    private static String TAG = "TaskPagerEditOnlyActivity";

    @Override
    protected Fragment createFragment() {
        Long taskID = (Long)getIntent()
                .getSerializableExtra(TaskPagerEditOnlyFragment.EXTRA_TASK_ID);
        return TaskPagerViewOnlyFragment.newInstance(taskID);

    }
}
