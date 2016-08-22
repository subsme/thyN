package com.thyn.connection;

import android.app.IntentService;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by shalu on 8/19/16.
 */
public class PollService extends IntentService{
    private static final String TAG = "PollService";

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
        connectToServer();
    }
    private void connectToServer(){
        new ReceiveFromServerAsyncTask(this).execute();
    }
}
