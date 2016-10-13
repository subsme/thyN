package com.thyn.connection;

import android.app.AlarmManager;
import android.app.IntentService;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by shalu on 8/19/16.
 */
public class PollService extends IntentService{
    private static final String TAG = "PollService";

    private static final int POLL_INTERVAL = 1000*15*60*60;

    public PollService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent){
        //Page 470 A big nerd ranch guide
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() != null;
        if(!isNetworkAvailable) return;
        Log.i(TAG, "Received an intent");
        Log.i(TAG, "GoogleAPIConnector local android run is: " + GoogleAPIConnector.isLocalAndroidRun());
        connectToServer();
    }
    private void connectToServer(){
        new ReceiveFromServerAsyncTask(this).execute();
    }

    public static void setServiceAlarm(Context context, boolean isOn){
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
        }
        else{
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}
