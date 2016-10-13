package com.thyn.task.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;


import com.thyn.SingleFragmentActivity;
import com.thyn.collection.Task;
import com.thyn.collection.MyTaskLab;
import com.thyn.R;
import com.thyn.task.TaskFragment;

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
