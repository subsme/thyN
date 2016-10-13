package com.thyn.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.CursorAdapter;

import com.thyn.collection.Task;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by shalu on 8/1/16.
 */
public class thynTaskDBHelper  extends SQLiteOpenHelper {
    private static String TAG                                           = "thynTaskDBHelper";
    private static final String DB_NAME                                 = "thyn.sqlite";
    private static final int VERSION                                    = 2;

    private  String TABLE_TASK;

    private static final String COLUMN_ID                               = "_id";
    private static final String COLUMN_TASK_ID                          = "taskId";
    private static final String COLUMN_TASK_DESCRIPTION                 = "taskDescription";
    private static final String COLUMN_TASK_BEGIN_LOCATION              = "begin_location";
    private static final String COLUMN_TASK_BEGIN_LOCATION_CITY         = "begin_location_city";
    private static final String COLUMN_TASK_BEGIN_LOCATION_LATITUDE     = "begin_location_latitude";
    private static final String COLUMN_TASK_BEGIN_LOCATION_LONGITUDE    = "begin_location_longitude";
    private static final String COLUMN_TASK_END_LOCATION                = "end_location";
    private static final String COLUMN_TASK_USER_PROFILE_KEY            = "user_profile_key";
    private static final String COLUMN_TASK_USER_PROFILE_NAME           = "user_profile_name";
    private static final String COLUMN_TASK_DATE_CREATED                = "date_created";
    private static final String COLUMN_TASK_DATE_UPDATED                = "date_updated";
    private static final String COLUMN_TASK_HELPER_PROFILE_KEY          = "helper_profile_key";
    private static final String COLUMN_TASK_HELPER_PROFILE_NAME         = "helper_profile_name";
    private static final String COLUMN_TASK_DATE_SERVICED               = "date_serviced";
    private static final String COLUMN_TASK_DATE_SERVICED_TO            = "date_serviced_to";
    private static final String COLUMN_TASK_TIME_SERVICED               = "time_serviced";
    private static final String COLUMN_TASK_USER_WAIT_RESPONSE_TIME     = "user_wait_response_time";
    private static final String COLUMN_TASK_IS_SOLVED                   = "is_solved";
    private static final String COLUMN_TASK_IMAGE_URL                   = "image_url";
    private static final String COLUMN_TASK_TITLE                       = "task_title";

    private int count                                            = 0;

    public thynTaskDBHelper(Context context, String TableName) {
        super(context, DB_NAME, null, VERSION);
        TABLE_TASK = TableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating table: "+ TABLE_TASK);
       try {
           db.execSQL(
                   "CREATE TABLE " + TABLE_TASK + " (" +
                           " _id integer primary key" +
                           ", taskId varchar(100)" +
                           ", taskDescription varchar(300)" +
                           ", task_title varchar(200)" +
                           ", image_url varchar(400)" +
                           ", begin_location varchar(100)" +
                           ", begin_location_city varchar(30)" +
                           ", begin_location_latitude real" +
                           ", begin_location_longitude real" +
                           ", end_location varchar(100)" +
                           ", user_profile_key integer " +
                           ", user_profile_name varchar(100)" +
                           ", date_created text" +
                           ", date_updated text" +
                           ", helper_profile_key integer" +
                           ", helper_profile_name varchar(100)" +
                           ", time_serviced text" +
                           ", date_serviced text" +
                           ", user_wait_response_time" +
                           ", is_solved integer" +
                           " )"
           );
       }
       catch(SQLException sqle){
           sqle.printStackTrace();
       }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertTask(Task task){
        count++;
        Log.d(TAG, "Inserting in local database("
                + TABLE_TASK
                + ") cache. Inserting row # "
                + count
                + ", TITLE: "
                + task.getTaskTitle());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TASK_ID,task.getId());
        cv.put(COLUMN_TASK_DESCRIPTION, task.getTaskDescription());
        cv.put(COLUMN_TASK_BEGIN_LOCATION, task.getBeginLocation());
        cv.put(COLUMN_TASK_BEGIN_LOCATION_CITY, task.getCity());
        cv.put(COLUMN_TASK_BEGIN_LOCATION_LATITUDE, task.getLAT());
        cv.put(COLUMN_TASK_BEGIN_LOCATION_LONGITUDE, task.getLONG());
        cv.put(COLUMN_TASK_END_LOCATION, task.getEndLocation());
        cv.put(COLUMN_TASK_USER_PROFILE_KEY,task.getUserProfileKey());
        cv.put(COLUMN_TASK_USER_PROFILE_NAME, task.getUserProfileName());
        cv.put(COLUMN_TASK_DATE_CREATED, task.getCreateDate().toString());
        cv.put(COLUMN_TASK_DATE_UPDATED, task.getCreateDate().toString());
        if(task.getHelperProfileKey() != null)
            cv.put(COLUMN_TASK_HELPER_PROFILE_KEY, task.getHelperProfileKey().toString());
        cv.put(COLUMN_TASK_HELPER_PROFILE_NAME, task.getHelperProfileName());
        cv.put(COLUMN_TASK_DATE_SERVICED, task.getServiceDate().toString());
        cv.put(COLUMN_TASK_TIME_SERVICED, task.getTaskTimeRange());
        cv.put(COLUMN_TASK_USER_WAIT_RESPONSE_TIME, task.getmWaitResponseTime());
        cv.put(COLUMN_TASK_IS_SOLVED, "");
        cv.put(COLUMN_TASK_IMAGE_URL,task.getImageURL());
        cv.put(COLUMN_TASK_TITLE, task.getTaskTitle());

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
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK,
                null,
                "helper_profile_key is null or helper_profile_key = ?",
                new String[]{""},
                null,
                null,
                COLUMN_TASK_DATE_CREATED + " asc");

        Log.d(TAG,"CUrsor count is: " + wrapped.getCount());
        return new TaskCursor(wrapped);
    }

    public TaskCursor queryTasks(int rows){
        //Equivalent to "select * from task order by date_created
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK,
                null,
                "helper_profile_key is null or helper_profile_key = ?",
                new String[]{""},
                null,
                null,
                COLUMN_TASK_DATE_CREATED + " asc",
                "2");

        Log.d(TAG," CUrsor count (only 2 supposed to be returned here) is: " + wrapped.getCount());
        return new TaskCursor(wrapped);
    }

    public TaskCursor queryMyTasks(Long uid){
        //Equivalent to "select * from task order by date_created
        Log.d(TAG, "Querying my tasks without no row limit. uid is: " + uid);
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK,
                null,
                COLUMN_TASK_USER_PROFILE_KEY + " =?",
                new String[]{String.valueOf(uid)},
                null,
                null,
                null);

        Log.d(TAG,"CUrsor count is: " + wrapped.getCount());
        return new TaskCursor(wrapped);
    }

    public TaskCursor queryMyTasks(Long uid, int numrows){
        //Equivalent to "select * from task order by date_created
        Log.d(TAG, "Querying my tasks with numrows. uid is: " + uid);
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK,
                null,
                COLUMN_TASK_USER_PROFILE_KEY + " =?",
                new String[]{String.valueOf(uid)},
                null,
                null,
                null,
                "2");

        Log.d(TAG,"CUrsor count is: " + wrapped.getCount());
        return new TaskCursor(wrapped);
    }
    public TaskCursor queryTasksIWillHelp(Long uid){
        //Equivalent to "select * from task order by date_created
        Log.d(TAG, "Querying the Tasks that I am willing to help. uid is: " + uid);
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK,
                null,
                COLUMN_TASK_HELPER_PROFILE_KEY + " =?",
                new String[]{String.valueOf(uid)},
                null,
                null,
                null);

        Log.d(TAG,"CUrsor count is: " + wrapped.getCount());
        return new TaskCursor(wrapped);
    }

    public TaskCursor queryTask(long id){
        Cursor wrapped = getReadableDatabase().query(TABLE_TASK,
                null,
                COLUMN_ID + " = ?",// look for a task id
                new String[]{String.valueOf(id)},// with this value
                null,
                null,
                null,
                "1");// limit to 1 row;*/
       /* String q = "select * from  " + TABLE_TASK + " where " + COLUMN_ID + " = ?";
        Log.d(TAG,q);
        Cursor wrapped = getReadableDatabase().rawQuery(q,new String[]{String.valueOf(id)});*/
        Log.d(TAG, wrapped == null?"null cursor":"not a null cursor");
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
            task.setTaskTitle(getString(getColumnIndex(COLUMN_TASK_TITLE)));
            task.setImageURL(getString(getColumnIndex(COLUMN_TASK_IMAGE_URL)));
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
            task.setCity(getString(getColumnIndex(COLUMN_TASK_BEGIN_LOCATION_CITY)));
            task.setLAT(getDouble(getColumnIndex(COLUMN_TASK_BEGIN_LOCATION_LATITUDE)));
            task.setLONG(getDouble(getColumnIndex(COLUMN_TASK_BEGIN_LOCATION_LONGITUDE)));

            String serviceDateStr = getString(getColumnIndex(COLUMN_TASK_DATE_SERVICED));
            SimpleDateFormat serviceDateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
            try {
                task.setTaskFromDate(serviceDateFormat.parse(serviceDateStr));
            }
            catch(ParseException e){
                e.printStackTrace();
            }

            task.setTaskTimeRange(getString(getColumnIndex(COLUMN_TASK_TIME_SERVICED)));


            return task;

        }
    }
/*Subu
Adding this helper function for AndroidDatabaseManager to work.
 */
public ArrayList<Cursor> getData(String Query){
    //get writable database
    SQLiteDatabase sqlDB = this.getWritableDatabase();
    String[] columns = new String[] { "mesage" };
    //an array list of cursor to save two cursors one has results from the query
    //other cursor stores error message if any errors are triggered
    ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
    MatrixCursor Cursor2= new MatrixCursor(columns);
    alc.add(null);
    alc.add(null);


    try{
        String maxQuery = Query ;
        //execute the query results will be save in Cursor c
        Cursor c = sqlDB.rawQuery(maxQuery, null);


        //add value to cursor2
        Cursor2.addRow(new Object[] { "Success" });

        alc.set(1,Cursor2);
        if (null != c && c.getCount() > 0) {


            alc.set(0,c);
            c.moveToFirst();

            return alc ;
        }
        return alc;
    } catch(SQLException sqlEx){
        Log.d("printing exception", sqlEx.getMessage());
        //if any exceptions are triggered save the error message to cursor an return the arraylist
        Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
        alc.set(1,Cursor2);
        return alc;
    } catch(Exception ex){

        Log.d("printing exception", ex.getMessage());

        //if any exceptions are triggered save the error message to cursor an return the arraylist
        Cursor2.addRow(new Object[] { ""+ex.getMessage() });
        alc.set(1,Cursor2);
        return alc;
    }


}
}
