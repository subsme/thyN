package photogallery.android.bignerdranch.com.thyn.collection;

import android.content.Context;

import com.thyn.backend.myTaskApi.model.MyTask;

import java.util.ArrayList;


/**
 * Created by shalu on 2/22/16.
 */
public class MyPersonalTaskLab extends TaskLab{

    private static MyPersonalTaskLab sTaskLab;


    private MyPersonalTaskLab(Context mAppContext){
        super(mAppContext);
    }

    public static MyPersonalTaskLab get(Context c){
        if(sTaskLab == null){
            sTaskLab = new MyPersonalTaskLab(c.getApplicationContext());
        }
        return sTaskLab;
    }
}
