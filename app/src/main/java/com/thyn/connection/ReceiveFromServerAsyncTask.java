package com.thyn.connection;

import android.app.Service;
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

    public ReceiveFromServerAsyncTask(PollService p) {
        super();
        userprofileid = MyServerSettings.getUserProfileId(p);
        this.service = p;
        if(manager == null) manager = MyTaskLab.get(p);
        /*Subu
        First thing is I am going to delete the current cache.
         */
        manager.purgeTasks();
    }

    @Override
    protected List doInBackground(Void... params) {
        List l = null;

        try {
            Log.d(TAG, "Sending user profile id:" + userprofileid);
            l = GoogleAPIConnector.connect_TaskAPI().listTasks(false, userprofileid, false).execute().getItems();
        } catch (IOException e) {
            e.getMessage();
        }

        return l;
    }

    @Override
    protected void onPostExecute(List result) {
        if (result == null) return;

        Iterator i = result.iterator();

        Log.d(TAG, "The data count sent from the server is: " + result.size());

        if(!MyServerSettings.getLocalTaskCache(service)){
            while (i.hasNext()) {
                MyTask myTask = (MyTask) i.next();
                Log.d(TAG, " Inserting Task. Description: " + myTask.getTaskDescription());
                manager.convertRemotetoLocalTask(myTask);
            }
            MyServerSettings.initializeLocalTaskCache(service.getApplicationContext());
        }

    }
}
