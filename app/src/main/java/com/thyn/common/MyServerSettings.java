package com.thyn.common;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import com.thyn.broadcast.GCMPreferences;
import com.thyn.collection.Filter;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.connection.PollService;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by shalu on 4/5/16.
 */
public class MyServerSettings {
    private static final String TAG = "MyServerSettings";
    public static int LocalDevServer = -1;
    public static int DevServer = 0;
    public static int ProdServer = 1;

    public static boolean LOCAL_DEBUG = false;

    private static int currentServer = 0;

    public static final String PREF_USERPROFILE_ID = "userProfileID";
    public static final String PREF_USERPROFILE_NAME = "userProfileName";
    public static final String PREF_USERIMAGE_URL = "userimageURL";
    public static final String PREF_USER_TOKEN = "userToken";
    public static final String PREF_USER_SOCIAL_ID = "userSocialID";
    public static final String PREF_USER_SOCIAL_TYPE = "userSocialType";
    public static final String PREF_USER_ADDRESS = "userAddress";
    public static final String PREF_USER_APT_NUMBER = "userAptNumber";
    public static final String PREF_USER_CITY = "userCity";
    public static final String PREF_USER_LANG = "userLang";
    public static final String PREF_USER_LAT = "userLat";
    public static final String PREF_USER_PHONE = "userPhone";
    public static final String PREF_LOCAL_FILE_LOCATION="local_File";

    public static String LOCAL_TASK_CACHE = "taskCache";
    private static String LOCAL_MYTASK_CACHE = "myTaskCache";

    private static String MY_POINTS = "myPoints";
    private static String NEIGHBRS_HELPED = "neighbrsHelped";
    private static String TOTAL_REQUESTS_WITHIN_RANGE = "totalRequestsWithinRange";

    private static final String PREF_FILTER_OPTION ="com.thyn.tab.filter";
    private static final String PREF_FILTER_RADIUS="com.thyn.tab.filter.radius";

    public static void initializeEnvironment(Context c){
        //setEnvironment(MyServerSettings.LocalDevServer);
        initializeIsTokenSent(c);

    }
    public static void writeLocalLogs(Context c){
        LOCAL_DEBUG = true;
        writeLogToFileSystem(c);
    }

    public static void startPolling(Context c){
        Intent i = new Intent(c, PollService.class);
        c.startService(i);
    }
    public static void setEnvironment(int server){
        if(server == DevServer){
            currentServer = DevServer;
        }
        else if(server == LocalDevServer){
            currentServer = LocalDevServer;
            GoogleAPIConnector.setLocalAndroidRun(true);
        }
        else currentServer = ProdServer;
    }

    public static int getEnvironment(){
        return currentServer;
    }

    public static void initializeIsTokenSent(Context context){
        SharedPreferences settings = context.getSharedPreferences(GCMPreferences.SENT_TOKEN_TO_SERVER, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        settings.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false).apply();
    }
    public static void initializeUserProfile(Context context, Long id, String firstname, String token, String image_url){
        clearDefaultCache(context);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(MyServerSettings.PREF_USERPROFILE_ID, id)
                .putString(MyServerSettings.PREF_USERPROFILE_NAME, firstname)
                .putString(MyServerSettings.PREF_USER_TOKEN, token)
                .putString(MyServerSettings.PREF_USERIMAGE_URL, image_url)
                .commit();
    }
    public static void initializeUserProfileID(Context context, Long id){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(MyServerSettings.PREF_USERPROFILE_ID, id)
                .commit();
    }
    public static void initializeUserProfile(Context context, int socialType, String socialid, String firstname, String token, String image_url){
        clearDefaultCache(context);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(MyServerSettings.PREF_USER_SOCIAL_TYPE, socialType)
                .putString(MyServerSettings.PREF_USER_SOCIAL_ID, socialid)
                .putString(MyServerSettings.PREF_USERPROFILE_NAME, firstname)
                .putString(MyServerSettings.PREF_USER_TOKEN, token)
                .putString(MyServerSettings.PREF_USERIMAGE_URL, image_url)
                .commit();
    }
    public static void clearDefaultCache(Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = settings.edit();
        editor.clear().commit();
    }
    public static Long getUserProfileId(Context c){
        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getLong(MyServerSettings.PREF_USERPROFILE_ID, -1);
    }
    public static String getUserSocialId(Context c){
        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getString(MyServerSettings.PREF_USER_SOCIAL_ID, null);
    }
    public static int getUserSocialType(Context c){
        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getInt(MyServerSettings.PREF_USER_SOCIAL_TYPE, -1);
    }

    public static String getUserProfileImageURL(Context c){

        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getString(MyServerSettings.PREF_USERIMAGE_URL, null);
    }
    public static void initializeLocalTaskCache(Context context){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(MyServerSettings.LOCAL_TASK_CACHE, true)
                .commit();
    }
    public static void initializeLocalTaskCache(Context context, boolean b){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(MyServerSettings.LOCAL_TASK_CACHE, b)
                .commit();
    }

    public static boolean getLocalTaskCache(Context c){
        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getBoolean(MyServerSettings.LOCAL_TASK_CACHE, false);
    }

    public static void initializePoints(Context context, int points){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(MyServerSettings.MY_POINTS, points)
                .commit();
    }
    public static void initializeUserName(Context context, String name){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(MyServerSettings.PREF_USERPROFILE_NAME, name)
                .commit();
    }
    public static String getUserName(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_USERPROFILE_NAME,null);
    }
    public static String getUserFirstName(Context c){
        String str = PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_USERPROFILE_NAME,null);
        if(str != null && str.contains(" ")){
            str = str.substring(0,str.indexOf(" "));
        }
        return str;
    }
    public static int getPoints(Context c){
        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getInt(MyServerSettings.MY_POINTS, -1);
    }
    public static void initializeNumNeighbrsIHelped(Context context, int num){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(MyServerSettings.NEIGHBRS_HELPED, num)
                .commit();
    }
    public static int getNumNeighbrsIHelped(Context c){
        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getInt(MyServerSettings.NEIGHBRS_HELPED, -1);
    }
    public static void initializeTotalRequestsWithinRange(Context context, int num){
    PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
    .putInt(MyServerSettings.TOTAL_REQUESTS_WITHIN_RANGE, num)
    .commit();
}
    public static int getTotalRequestsWithinRange(Context c){
        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getInt(MyServerSettings.TOTAL_REQUESTS_WITHIN_RANGE, -1);
    }
    public static void initializeUserAddress(Context context, String address, String aptNo, String city, String lat, String lng){

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(MyServerSettings.PREF_USER_ADDRESS, address)
                .putString(MyServerSettings.PREF_USER_APT_NUMBER, aptNo)
                .putString(MyServerSettings.PREF_USER_CITY, city)
                .putString(MyServerSettings.PREF_USER_LAT, lat)
                .putString(MyServerSettings.PREF_USER_LANG, lng)
                .commit();
    }
    public static String getUserAddress(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_USER_ADDRESS, null);
    }
    public static String getUserAptNumber(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_USER_APT_NUMBER, null);
    }
    public static void initializeUserPhone(Context context, String phone){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(MyServerSettings.PREF_USER_PHONE, phone)
                .commit();
    }
    public static String getUserPhone(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_USER_PHONE,null);
    }

    public static String getUserCity(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_USER_CITY,null);
    }

    public static String getUserLNG(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_USER_LANG,null);
    }

    public static String getUserLAT(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_USER_LAT,null);
    }
    public static String getLocalFileLocation(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(MyServerSettings.PREF_LOCAL_FILE_LOCATION,null);
    }

    public static void writeLogToFileSystem(Context c){
        if ( isExternalStorageWritable() ) {

            File appDirectory = new File( Environment.getExternalStorageDirectory() + "/thyNeighbr" );
            File logDirectory = new File( appDirectory + "/log" );
            File logFile = new File( logDirectory, "thyNeighbr-logcat" + System.currentTimeMillis() + ".txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }
            // clear the previous logcat and then write the new one to the file
            try {
                Log.d(TAG, "Writing logs to " + logFile.toString());
                PreferenceManager.getDefaultSharedPreferences(c)
                        .edit()
                        .putString(MyServerSettings.PREF_LOCAL_FILE_LOCATION, logFile.toString())
                        .commit();
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }
    }
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals(state) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }
    public static void initializeFilterSettings(Context context, Filter filter){
        int filterSetting = 0;
        if(filter.closestToMyHome){
            Log.d(TAG, "Setting closestto my home");
            filterSetting = 1;
        }
        else if(filter.expiringSoon){
            Log.d(TAG, "Setting expiring soon");
            filterSetting = 2;
        }
        else if(filter.mostRecent){
            Log.d(TAG, "Setting mostrecent");
            filterSetting = 3;
        }
        if(filter.distanceRadius <= 0) filter.distanceRadius = 20;
        Log.d(TAG, "setting distance radius: " + filter.distanceRadius);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(MyServerSettings.PREF_FILTER_OPTION, filterSetting)
                .putInt(MyServerSettings.PREF_FILTER_RADIUS, filter.distanceRadius)
                .commit();
    }
    public static int getFilterSetting(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(MyServerSettings.PREF_FILTER_OPTION,1);
    }

    public static int getFilterRadius(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(MyServerSettings.PREF_FILTER_RADIUS,20);
    }

}
