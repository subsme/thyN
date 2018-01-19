package com.thyn.task.edit;

import android.support.v4.app.Fragment;

import com.thyn.SingleFragmentActivity;

public class TaskActivity extends SingleFragmentActivity {

   @Override
    protected Fragment createFragment() {
       Long taskID = (Long)getIntent()
               .getSerializableExtra(TaskFragment.EXTRA_TASK_ID);

       return TaskFragment.newInstance(taskID);

   }
}
