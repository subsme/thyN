package com.thyn.collection;

import android.content.Context;
import android.util.Log;

import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.db.thynTaskDBHelper;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by shalu on 3/24/16.
 */
public class TaskLab {
    private static String TAG                                           = "TaskLab";
    //private ArrayList<Task> mTasks;
    private Context mAppContext;
    private thynTaskDBHelper dbHelper;


    protected TaskLab(Context mAppContext, String TableName){
        Log.i(TAG, "Initializing TaskLab constructor and therefore dbHelper");
        this.mAppContext = mAppContext;
        this.dbHelper = new thynTaskDBHelper(mAppContext, TableName);
      //  mTasks = new ArrayList<Task>();

    }
    public void addTask(Task t){
        //mTasks.add(t);
        this.dbHelper.insertTask(t);
    }
    public thynTaskDBHelper.TaskCursor queryTasks(){
        return this.dbHelper.queryTasks();
    }
    public thynTaskDBHelper.TaskCursor queryTasks(int rows){
        return this.dbHelper.queryTasks(rows);
    }
    /*public ArrayList<Task> getTasks(){
        return mTasks;
    }
    public Task getTask(Long id){
        for(Task t:mTasks){
            if(t.getId().equals(id)) return t;
        }
        return null;
    }*/
    /*public boolean removeTask(Task t){
        return mTasks.remove(t);
    }
    public void removeAllTasks(){
        mTasks.clear();
    }*/
    public void purgeTasks(){
        Log.d(TAG, "purgeTable");
        this.dbHelper.purgeTable();
    }

   public  void convertRemotetoLocalTask(MyTask t){
        Task a = new Task();

        a.setId(t.getId());
        a.setEndLocation(t.getEndLocation());
        a.setBeginLocation(t.getBeginLocation());
        a.setTaskDescription(t.getTaskDescription());
        a.setUserProfileName(t.getUserProfileName());
        a.setUserProfileKey(t.getUserProfileKey());
        a.setHelperProfileName(t.getHelperProfileName());

        SimpleDateFormat sdFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
        try {
            a.setCreateDate(sdFormat.parse(t.getCreateDate()));
            a.setServiceDate(sdFormat.parse(t.getServiceDate()));
        }
        catch(ParseException e){
            e.printStackTrace();
        }
       if(t.getHelperUserProfileKey() != null){
           a.setIsAccepted(true);
       }
       else
           a.setIsAccepted(false);

       //see if this task already exists in the database. if it does, don't insert it. otherwise insert it.
       addTask(a);
    }

    public boolean isInCache(){
        long n = dbHelper.countRecords();
        if(n > 0) {
            Log.d(TAG, "No. of cache elements are : " + n);
            return true;
        }
        return false;
    }

    public Task getTask(long id){
        Task task = null;
        thynTaskDBHelper.TaskCursor cursor = dbHelper.queryTask(id);
        cursor.moveToFirst();
        //if you got a row, get a task
        if(!cursor.isAfterLast())
                task = cursor.getTask();
        cursor.close();
        return task;
    }

}
