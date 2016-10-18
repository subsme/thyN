package com.thyn.common;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import com.thyn.broadcast.GCMPreferences;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.connection.PollService;

import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;

/**
 * Created by shalu on 4/5/16.
 */
public class MyServerSettings {
    public static int LocalDevServer = -1;
    public static int DevServer = 0;
    public static int ProdServer = 1;

    private static int currentServer = 0;

    public static final String PREF_USERPROFILE_ID = "userProfileID";
    public static final String PREF_USERPROFILE_NAME = "userProfileName";
    public static final String PREF_USERIMAGE_URL = "userimageURL";
    public static final String PREF_USER_TOKEN = "userToken";
    public static final String PREF_USER_SOCIAL_ID = "userSocialID";
    public static final String PREF_USER_SOCIAL_TYPE = "userSocialType";

    public static String LOCAL_TASK_CACHE = "taskCache";
    private static String LOCAL_MYTASK_CACHE = "myTaskCache";

    private static String MY_POINTS = "myPoints";
    private static String NEIGHBRS_HELPED = "neighbrsHelped";
    private static String TOTAL_REQUESTS_WITHIN_RANGE = "totalRequestsWithinRange";

    public static void initializeEnvironment(Context c){
        //setEnvironment(MyServerSettings.LocalDevServer);

        initializeIsTokenSent(c);

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

}
