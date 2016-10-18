package com.thyn.connection;

import android.app.Service;
import android.os.AsyncTask;
import android.util.Log;

import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.android.backend.userAPI.model.APIUserInformation;
import com.thyn.collection.MyTaskLab;
import com.thyn.common.MyServerSettings;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ReceiveStatsFromServerAsyncTask extends AsyncTask<Void, Void, APIUserInformation> {
    private static final String TAG = "ReceiveStatsFromServerAsyncTask";
    int numItems;
    Long userprofileid;
    Service service;
    private MyTaskLab manager;
    PollService p;

    public ReceiveStatsFromServerAsyncTask(PollService p) {
        super();
        this.p = p;
        userprofileid = MyServerSettings.getUserProfileId(p);
        this.service = p;
        if(manager == null) manager = MyTaskLab.get(p);

    }

    @Override
    protected APIUserInformation doInBackground(Void... params) {
        APIUserInformation userStats = null;

        try {
            Log.d(TAG, "Sending user profile id:" + userprofileid);
            userStats = GoogleAPIConnector.connect_UserAPI().getUserStats(userprofileid,20).execute();
        } catch (IOException e) {
            e.getMessage();
            Log.d(TAG,e.getMessage());
        }

        return userStats;
    }

    @Override
    protected void onPostExecute(APIUserInformation result) {

        if (result == null){
            Log.d(TAG, "Nothing obtained from server");
            return;
        }

        Log.d(TAG, "Neighbors helped: " + result.getNumNeighbrsHelped()
                + "points gathered : "  + result.getThyNPoints()
               );

        MyServerSettings.initializeNumNeighbrsIHelped(p, result.getNumNeighbrsHelped());
        MyServerSettings.initializePoints(p, result.getThyNPoints());


    }
}
