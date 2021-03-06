package com.thyn.connection;

import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.collection.MyTaskLab;
import com.thyn.common.MyServerSettings;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ReceiveFromServerAsyncTask extends AsyncTask<Void, Void, List> {
    private static final String TAG = "ReceiveFromServerAsyncTask";
    int numItems;
    Long userprofileid;
    Service service;
    private MyTaskLab manager;
    private Context c;

    public ReceiveFromServerAsyncTask(PollService p, Context c) {
        super();
        userprofileid = MyServerSettings.getUserProfileId(p);
        this.service = p;
        if(manager == null) manager = MyTaskLab.get(p);
        this.c = c;
        /*Subu
        First thing is I am going to delete the current cache.
         */
        manager.purgeTasks();
    }

    @Override
    protected List doInBackground(Void... params) {
        List l = null;

        try {

            Log.d(TAG, "Sending user profile id:" + MyServerSettings.getUserSocialId(c));
            //l = GoogleAPIConnector.connect_TaskAPI().listTasks(false, Long.valueOf(userprofileid), false).execute().getItems();
            l = GoogleAPIConnector.connect_TaskAPI().listTasks(MyServerSettings.getFilterRadius(c), false, MyServerSettings.getUserSocialId(c), MyServerSettings.getUserSocialType(c), false).execute().getItems();
        } catch (IOException e) {
            e.getMessage();
            Log.d(TAG,e.getMessage());
        }

        return l;
    }

    @Override
    protected void onPostExecute(List result) {
        if (result == null) return;

        Iterator i = result.iterator();

        Log.d(TAG, "The data count sent from the server is: " + result.size());
        MyServerSettings.initializeTotalRequestsWithinRange(service.getApplicationContext(),result.size());

        if(!MyServerSettings.getLocalTaskCache(service)){
            while (i.hasNext()) {
                MyTask myTask = (MyTask) i.next();
                Log.d(TAG, " Inserting Task. Description: " + myTask.getTaskTitle() + ", Distance: " + myTask.getDistanceFromOrigin());
                manager.convertRemotetoLocalTask(myTask);
            }
            MyServerSettings.initializeLocalTaskCache(service.getApplicationContext());
        }

    }
}
