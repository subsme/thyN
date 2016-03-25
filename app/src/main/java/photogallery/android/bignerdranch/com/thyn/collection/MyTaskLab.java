package photogallery.android.bignerdranch.com.thyn.collection;

import android.content.Context;

import java.util.ArrayList;

import com.thyn.backend.myTaskApi.model.MyTask;



/**
 * Created by shalu on 2/22/16.
 */
public class MyTaskLab extends TaskLab{


    private static MyTaskLab sTaskLab;

    private MyTaskLab(Context mAppContext){
        super(mAppContext);
    }

    public static MyTaskLab get(Context c){
        if(sTaskLab == null){
            sTaskLab = new MyTaskLab(c.getApplicationContext());
        }
        return sTaskLab;
    }
}
