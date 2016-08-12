package com.thyn.collection;

import android.content.Context;


/**
 * Created by shalu on 2/22/16.
 */
public class MyTaskLab extends TaskLab{


    private static MyTaskLab sTaskLab;
    private static String tableName = "task";

    private MyTaskLab(Context mAppContext){
        super(mAppContext, tableName);
    }

    public static MyTaskLab get(Context c){
        if(sTaskLab == null){
            sTaskLab = new MyTaskLab(c.getApplicationContext());
        }
        return sTaskLab;
    }
}
