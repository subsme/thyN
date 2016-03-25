package photogallery.android.bignerdranch.com.thyn.collection;

import android.content.Context;

import com.thyn.backend.myTaskApi.model.MyTask;

import java.util.ArrayList;


/**
 * Created by shalu on 2/22/16.
 */
public class MyCompletedTaskLab extends TaskLab{

    private static MyCompletedTaskLab sTaskLab;


    private MyCompletedTaskLab(Context mAppContext){
        super(mAppContext);
    }

    public static MyCompletedTaskLab get(Context c){
        if(sTaskLab == null){
            sTaskLab = new MyCompletedTaskLab(c.getApplicationContext());
        }
        return sTaskLab;
    }
}
