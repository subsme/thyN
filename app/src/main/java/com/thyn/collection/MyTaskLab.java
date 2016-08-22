package com.thyn.collection;

import android.content.Context;
import android.util.Log;


/**
 * Created by shalu on 2/22/16.
 */
public class MyTaskLab extends TaskLab{


    private static MyTaskLab sTaskLab;
    private static String tableName = "task";
    private static String TAG                                           = "MyTaskLab";

    private MyTaskLab(Context mAppContext){
        super(mAppContext, tableName);
    }

    public static MyTaskLab get(Context c){
        if(sTaskLab == null){
            Log.d(TAG, "No MyTaskLab object exists. Creating new MyTaskLab instance");
            sTaskLab = new MyTaskLab(c.getApplicationContext());
        }
        else Log.d(TAG, "MyTaskLab object already exists. Returning the already created MyTaskLab instance");
        return sTaskLab;
    }
}
