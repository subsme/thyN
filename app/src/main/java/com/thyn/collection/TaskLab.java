package com.thyn.collection;

import android.content.Context;
import android.util.Log;

import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.common.MyServerSettings;
import com.thyn.db.thynTaskDBHelper;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by shalu on 3/24/16.
 */
public class TaskLab {
    private static String TAG                                           = "TaskLab";
    private ArrayList<Task> mTasks;
    private Context mAppContext;
    protected thynTaskDBHelper dbHelper;


    protected TaskLab(Context mAppContext, String TableName){
        Log.i(TAG, "Initializing TaskLab constructor and therefore dbHelper");
        this.mAppContext = mAppContext;
        this.dbHelper = new thynTaskDBHelper(mAppContext, TableName);
        mTasks = new ArrayList<Task>();

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
    public thynTaskDBHelper.TaskCursor queryMyTasks(Long myId){
        return this.dbHelper.queryMyTasks(myId);
    }
    public thynTaskDBHelper.TaskCursor queryMyTasks(Long myId, int rows){
        return this.dbHelper.queryMyTasks(myId, rows);
    }
    public thynTaskDBHelper.TaskCursor queryTasksIWillHelp(Long myId){
        return this.dbHelper.queryTasksIWillHelp(myId);
    }

    public ArrayList<Task> getTasks(){
        return mTasks;
    }
    /*public Task getTask(Long id){
        for(Task t:mTasks){
            if(t.getId().equals(id)) return t;
        }
        return null;
    }*/
    public boolean removeTask(Task t){
        return mTasks.remove(t);
    }
    public void removeAllTasks(){
        mTasks.clear();
    }
    public void purgeTasks(){
        Log.d(TAG, "purgeTable");
        MyServerSettings.initializeLocalTaskCache(mAppContext,false);
        this.dbHelper.purgeTable();
    }
    public boolean doesLocalCacheExist(){
        return MyServerSettings.getLocalTaskCache(mAppContext);
    }
    public void initializeLocalCache(){
        MyServerSettings.initializeLocalTaskCache(mAppContext);
    }

   public  void convertRemotetoLocalTask(MyTask t){
        Task a = new Task();
        Log.d(TAG, "Copying MyTask into Task...");
        Log.d(TAG, "Title: " + t.getTaskTitle()
                + ",Description: " + t.getTaskDescription()
                + ",Image URL: " + t.getImageURL()
                + ", Distance: " + t.getDistanceFromOrigin()
                + ", create date: " + t.getCreateDate()
                + ", update date: " + t.getUpdateDate());

        a.setId(t.getId());
        a.setEndLocation(t.getEndLocation());
        a.setBeginLocation(t.getBeginLocation());
        a.setTaskDescription(t.getTaskDescription());
        a.setUserProfileName(t.getUserProfileName());
        a.setUserProfileKey(t.getUserProfileKey());
        a.setHelperProfileKey(t.getHelperUserProfileKey());
        a.setHelperProfileName(t.getHelperProfileName());
        a.setTaskTitle(t.getTaskTitle());
        a.setImageURL(t.getImageURL());
        a.setLAT(t.getLat());
        a.setLONG(t.getLong());
        a.setCity(t.getCity());
        a.setTaskTimeRange(t.getServiceTimeRange());
        a.setDistance(t.getDistanceFromOrigin());

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        //TimeZone tz = TimeZone.getDefault();
        //TimeZone tz1 = TimeZone.getTimeZone("");
       //need to use joda time. this is looking crazy.
       // Log.d(TAG, "TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezone id :: " +tz.getID());
        //Log.d(TAG, "TimeZone   "+tz1.getDisplayName(false, TimeZone.SHORT)+" Timezone id :: " +tz1.getID());
       /* Subu  11/02 by setting the time zone to "", i am able to get the correct time. dont have to set to pst. weird.
       * need to do somemore testing in other phones. if thins are weird, have to investigate the use of joda time.*/
       sdFormat.setTimeZone(TimeZone.getTimeZone(""));
        try {

            if(t.getCreateDate() != null){
                a.setCreateDate(sdFormat.parse(t.getCreateDate().toString()));
                Log.d(TAG,"create date converted: "  +a.getCreateDate());
            }
            if(t.getUpdateDate() != null) a.setUpdateDate(sdFormat.parse(t.getUpdateDate().toString()));

            if(t.getServiceDate() != null){
                a.setServiceDate(sdFormat.parse(t.getServiceDate().toString()));
                a.setTaskFromDate(sdFormat.parse(t.getServiceDate().toString()));
            }

        }
        catch(ParseException e){
            Log.d(TAG, "Exception caught - " +  e.getMessage());
        }
       if(t.getHelperUserProfileKey() != null){
           a.setIsAccepted(true);
       }
       else
           a.setIsAccepted(false);

       //see if this task already exists in the database. if it does, don't insert it. otherwise insert it.
       addTask(a);
    }

    /*public boolean isInCache(){
        long n = dbHelper.countRecords();
        if(n > 0) {
            Log.d(TAG, "No. of cache elements are : " + n);
            return true;
        }
        return false;

    }*/

    public Task getTask(long id){
        Task task = null;
        Log.d(TAG, "I am in getTask(" + id + ")");
        thynTaskDBHelper.TaskCursor cursor = dbHelper.queryTask(id);
        cursor.moveToFirst();
        //if you got a row, get a task
        if(!cursor.isAfterLast())
                task = cursor.getTask();
        cursor.close();
        return task;
    }

}
