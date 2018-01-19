package com.thyn.task.view.iwillhelp;

import android.support.v4.app.Fragment;


import com.thyn.SingleFragmentActivity;

/**
 * Created by shalu on 2/23/16.
 */
public class TaskIWillHelpPagerViewOnlyActivity extends SingleFragmentActivity {

    private static String TAG = "TaskIWillHelpPagerViewOnlyActivity";

    @Override
    protected Fragment createFragment() {
        //String chatroomBroadcast = (String)getIntent().getSerializableExtra("broadcast");

        //if(chatroomBroadcast == null) {
            Long taskID = (Long) getIntent()
                    .getSerializableExtra(com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment.EXTRA_TASK_ID);
            return TaskIWillHelpPagerViewOnlyFragment.newInstance(taskID);
       // }
        /*else{
            String taskID = (String) getIntent()
                    .getSerializableExtra(com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment.EXTRA_TASK_ID);
            Long taskIDLong = null;
            if(taskID != null) {
                taskIDLong = Long.parseLong(taskID);
            }
            return ChatRoomFragment.newInstance(taskIDLong,false);
        }*/

    }

}
