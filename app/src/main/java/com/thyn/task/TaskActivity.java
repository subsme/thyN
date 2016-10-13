package com.thyn.task;

import android.support.v4.app.Fragment;

import com.thyn.SingleFragmentActivity;

public class TaskActivity extends SingleFragmentActivity {

   @Override
    protected Fragment createFragment() {
       Long taskID = (Long)getIntent()
               .getSerializableExtra(TaskFragment.EXTRA_TASK_ID);
       String operation = (String)getIntent()
               .getSerializableExtra(TaskFragment.OPERATION);
       return TaskFragment.newInstance(taskID,operation);

   }
}
