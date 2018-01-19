package com.thyn.task.edit;

import android.support.v4.app.Fragment;

import com.thyn.SingleFragmentActivity;

/**
 * Created by Subu Sundaram on 11/27/17.
 */

public class TaskEditActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        Long taskID = (Long)getIntent()
                .getSerializableExtra(TaskFragment.EXTRA_TASK_ID);

        return TaskEditFragment.newInstance(taskID);

    }
}
