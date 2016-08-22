package com.thyn.collection;

import android.content.Context;
import android.util.Log;

import com.thyn.db.thynTaskDBHelper;


/**
 * Created by shalu on 2/22/16.
 */
public class MyPersonalTaskLab extends TaskLab{

    private static MyPersonalTaskLab sTaskLab;
    private static String tableName = "task";
    private static String TAG                                           = "MyPersonalTaskLab";

    private MyPersonalTaskLab(Context mAppContext){
        super(mAppContext, tableName);
    }

    public static MyPersonalTaskLab get(Context c){
        if(sTaskLab == null){
            Log.d(TAG, "No MyPersonalTaskLab object exists. Creating new MyPersonalTaskLab instance");
            sTaskLab = new MyPersonalTaskLab(c.getApplicationContext());
        }
        else Log.d(TAG, "MyPersonalTaskLab object already exists. Returning the already created MyPersonalTaskLab instance");
        return sTaskLab;
    }

}
