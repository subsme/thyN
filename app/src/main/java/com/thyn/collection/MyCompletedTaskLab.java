package com.thyn.collection;

import android.content.Context;


/**
 * Created by shalu on 2/22/16.
 */
public class MyCompletedTaskLab extends TaskLab{

    private static MyCompletedTaskLab sTaskLab;
    private static String tableName = "task";

    private MyCompletedTaskLab(Context mAppContext){
        super(mAppContext,tableName);
    }

    public static MyCompletedTaskLab get(Context c){
        if(sTaskLab == null){
            sTaskLab = new MyCompletedTaskLab(c.getApplicationContext());
        }
        return sTaskLab;
    }
}
