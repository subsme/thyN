package com.thyn.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.CursorAdapter;

import com.thyn.collection.Task;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by shalu on 8/1/16.
 */
public class thynTaskDBHelper  extends SQLiteOpenHelper {
    private static String TAG                                           = "thynTaskDBHelper";
    private static final String DB_NAME                                 = "thyn.sqlite";
    private static final int VERSION                                    = 1;

    private static  String TABLE_TASK;

    private static final String COLUMN_TASK_ID                          = "taskId";
    private static final String COLUMN_TASK_DESCRIPTION                 = "taskDescription";
    private static final String COLUMN_TASK_BEGIN_LOCATION              = "begin_location";
    private static final String COLUMN_TASK_END_LOCATION                = "end_location";
    private static final String COLUMN_TASK_USER_PROFILE_KEY            = "user_profile_key";
    private static final String COLUMN_TASK_USER_PROFILE_NAME           = "user_profile_name";
    private static final String COLUMN_TASK_DATE_CREATED                = "date_created";
    private static final String COLUMN_TASK_DATE_UPDATED                = "date_updated";
    private static final String COLUMN_TASK_HELPER_PROFILE_KEY          = "helper_profile_key";
    private static final String COLUMN_TASK_HELPER_PROFILE_NAME         = "helper_profile_name";
    private static final String COLUMN_TASK_DATE_SERVICED               = "date_serviced";
    private static final String COLUMN_TASK_USER_WAIT_RESPONSE_TIME     = "user_wait_response_time";
    private static final String COLUMN_TASK_IS_SOLVED                   = "is_solved";

    private static int count                                            = 0;

    public thynTaskDBHelper(Context context, String TableName) {
        super(context, DB_NAME, null, VERSION);
        TABLE_TASK = TableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_TASK + " (" +
                        " _id integer primary key" +
                        ", taskId varchar(100)" +
                        ", taskDescription varchar(300)" +
                        ", begin_location varchar(100)" +
                        ", end_location varchar(100)" +
                        ", user_profile_key integer " +
                        ", user_profile_name varchar(100)" +
                        ", date_created text" +
                        ", date_updated text" +
                        ", helper_profile_key integer" +
                        ", helper_profile_name varchar(100)" +
                        ", date_serviced text" +
                        ", user_wait_response_time" +
                        ", is_solved integer" +
                        " )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertTask(Task task){
        count++;
        Log.i(TAG, "Inserting in local database cache. Inserting row # " + count);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TASK_ID,task.getId());
        cv.put(COLUMN_TASK_DESCRIPTION, task.getTaskDescription());
        cv.put(COLUMN_TASK_BEGIN_LOCATION, task.getBeginLocation());
        cv.put(COLUMN_TASK_END_LOCATION, task.getEndLocation());
        cv.put(COLUMN_TASK_USER_PROFILE_KEY,task.getUserProfileKey());
        cv.put(COLUMN_TASK_USER_PROFILE_NAME,task.getUserProfileName());
        cv.put(COLUMN_TASK_DATE_CREATED, task.getCreateDate().toString());
        cv.put(COLUMN_TASK_DATE_UPDATED, task.getCreateDate().toString());
        cv.put(COLUMN_TASK_HELPER_PROFILE_KEY, "");
        cv.put(COLUMN_TASK_HELPER_PROFILE_NAME, task.getHelperProfileName());
        cv.put(COLUMN_TASK_DATE_SERVICED, task.getServiceDate().toString());
        cv.put(COLUMN_TASK_USER_WAIT_RESPONSE_TIME, task.getmWaitResponseTime());
        cv.put(COLUMN_TASK_IS_SOLVED, "");

        return getWritableDatabase().insert(TABLE_TASK, null, cv);
    }


    /**
     * Remove all users  from database.
     */
    public void purgeTable()
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TASK, null, null);
    }

    /** Count no. of records in the table */
    public long countRecords(){
        SQLiteDatabase db = getReadableDatabase();
        if(db == null) return -1;
        long num = DatabaseUtils.queryNumEntries(db, TABLE_TASK);
        Log.d(TAG, "Number of items is: " + num);
        return num;
    }

    public TaskCursor queryTasks(){
        //Equivalent to "select * from task order by date_created
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK, null, null, null, null, null, COLUMN_TASK_DATE_CREATED + " asc");

        Log.d(TAG,"CUrsor count is: " + wrapped.getCount());
        return new TaskCursor(wrapped);
    }

    public TaskCursor queryTasks(int rows){
        //Equivalent to "select * from task order by date_created
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK, null, null, null, null, null, COLUMN_TASK_DATE_CREATED + " asc","2");

        Log.d(TAG," CUrsor count (only 2 supposed to be returned here) is: " + wrapped.getCount());
        return new TaskCursor(wrapped);
    }

    public TaskCursor queryTask(long id){
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK,
                null,
                COLUMN_TASK_ID + " = ?",// look for a task id
                new String[]{String.valueOf(id)},// with this value
                null,
                null,
                null,
                "1");// limit to 1 row;
        return new TaskCursor(wrapped);
    }
    /* A convenience class to wrap the cursor.
    page 549 anroid book - big nerd ranch
     */
    public static class TaskCursor extends CursorWrapper {
        public TaskCursor(Cursor c){
            super(c);
        }
        /* Returns a Task object
        configured for the current row
         */
        public Task getTask(){
            if (isBeforeFirst() || isAfterLast()) return null;

            Task task = new Task();
            task.setId(getLong(getColumnIndex(COLUMN_TASK_ID)));
            task.setUserProfileKey(getLong(getColumnIndex(COLUMN_TASK_USER_PROFILE_KEY)));
            task.setTaskDescription(getString(getColumnIndex(COLUMN_TASK_DESCRIPTION)));
            String strDate = getString(getColumnIndex(COLUMN_TASK_DATE_CREATED));
            SimpleDateFormat sdFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z y");
            try {
                task.setCreateDate(sdFormat.parse(strDate));
            }
            catch(ParseException e){
                e.printStackTrace();
            }
            task.setUserProfileName(getString(getColumnIndex(COLUMN_TASK_USER_PROFILE_NAME)));
            task.setBeginLocation(getString(getColumnIndex(COLUMN_TASK_BEGIN_LOCATION)));
            return task;

        }
    }

}
