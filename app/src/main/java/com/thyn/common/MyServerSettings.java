package com.thyn.common;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import com.thyn.broadcast.GCMPreferences;
import com.thyn.connection.PollService;

import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;
import android.support.v4.app.FragmentActivity;

/**
 * Created by shalu on 4/5/16.
 */
public class MyServerSettings {
    public static int DevServer = 0;
    public static int ProdServer = 1;

    private static int currentServer = 0;

    public static final String PREF_USERPROFILE_ID = "userProfileID";
    public static final String PREF_USERPROFILE_NAME = "userProfileName";

    private static String LOCAL_TASK_CACHE = "taskCache";
    private static String LOCAL_MYTASK_CACHE = "myTaskCache";

    public static void initializeEnvironment(Context c, Activity a){
        setEnvironment(MyServerSettings.DevServer);
        clearDefaultCache(c);
        initializeIsTokenSent(c);

        Intent i = new Intent(a, PollService.class);
        a.startService(i);
    }

    public static void setEnvironment(int server){
        if(server == DevServer){
           currentServer = DevServer;
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
    public static void initializeUserProfile(Context context, Long id, String firstname){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(MyServerSettings.PREF_USERPROFILE_ID, id)
                .putString(MyServerSettings.PREF_USERPROFILE_NAME, firstname)
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

}
