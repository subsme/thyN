package com.thyn.collection;

import android.content.Context;
import android.util.Log;

import com.thyn.db.thynTaskDBHelper;


/**
 * Created by shalu on 2/22/16.
 */
public class MyPersonalTaskLab extends TaskLab{

    private static MyPersonalTaskLab sTaskLab;
    private static String tableName = "mytask";


    private MyPersonalTaskLab(Context mAppContext){
        super(mAppContext, tableName);
    }

    public static MyPersonalTaskLab get(Context c){
        if(sTaskLab == null){
            sTaskLab = new MyPersonalTaskLab(c.getApplicationContext());
        }
        return sTaskLab;
    }

}
