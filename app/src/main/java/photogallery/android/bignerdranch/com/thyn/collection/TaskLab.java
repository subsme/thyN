package photogallery.android.bignerdranch.com.thyn.collection;

import android.content.Context;
import com.thyn.backend.myTaskApi.model.MyTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by shalu on 3/24/16.
 */
public class TaskLab {
    private ArrayList<Task> mTasks;
    private Context mAppContext;

    protected TaskLab(Context mAppContext){
        this.mAppContext = mAppContext;
        mTasks = new ArrayList<Task>();
    }
    public void addTask(Task t){
        mTasks.add(t);
    }
    public ArrayList<Task> getTasks(){
        return mTasks;
    }
    public Task getTask(Long id){
        for(Task t:mTasks){
            if(t.getId().equals(id)) return t;
        }
        return null;
    }
    public boolean removeTask(Task t){
        return mTasks.remove(t);
    }
    public void removeAllTasks(){
        mTasks.clear();
    }

   public  void convertRemotetoLocalTask(MyTask t){
        Task a = new Task();

        a.setId(t.getId());
        a.setEndLocation(t.getEndLocation());
        a.setBeginLocation(t.getBeginLocation());
        a.setTaskDescription(t.getTaskDescription());
        a.setUserProfileName(t.getUserProfileName());
        SimpleDateFormat sdFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
        try {
            a.setCreateDate(sdFormat.parse(t.getCreateDate()));
            a.setServiceDate(sdFormat.parse(t.getServiceDate()));
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        addTask(a);
    }
}
