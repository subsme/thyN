package com.thyn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by shalu on 3/30/16.
 */
public class StartupReceiver extends BroadcastReceiver{
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent){
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
    }
}
