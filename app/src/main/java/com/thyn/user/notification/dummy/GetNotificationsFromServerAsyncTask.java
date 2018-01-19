package com.thyn.user.notification.dummy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.thyn.android.backend.myTaskApi.model.MyTask;
import com.thyn.android.backend.userAPI.model.Message;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.user.notification.MyNotificationRecyclerViewAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by shalu on 12/15/16.
 */
public class GetNotificationsFromServerAsyncTask extends AsyncTask<Void, Void, List> {
   private Long userProfileId;
    private String TAG = "GetNotificationsFromServerAsyncTask";
    MyNotificationRecyclerViewAdapter adapter;
    public GetNotificationsFromServerAsyncTask(Long userProfileId, MyNotificationRecyclerViewAdapter adapter) {
        super();
        this.userProfileId = userProfileId;
        this.adapter = adapter;
    }

    @Override
    protected List doInBackground(Void... params) {
        List l = null;

        try {
            Log.d(TAG, "Sending user profile id:" + userProfileId);
            l = GoogleAPIConnector.connect_UserAPI().mynotifications(userProfileId).execute().getItems();


        } catch (IOException e) {
            e.getMessage();
        }

        return l;
    }

    @Override
    protected void onPostExecute(List result) {
        if (result == null) return;

        Iterator i = result.iterator();

        Log.d(TAG, "Trying to get notifications from the server");
        int j = 1;
        while (i.hasNext()) {
            Message message = (Message) i.next();
            Log.d(TAG, " Inserting Message: TaskName: " + message.getTaskName() + ", message: " + message.getContent());
            DummyContent.MessageItem item = new DummyContent.MessageItem(j+"", convertDateToString(message.getMessageTime()), message.getContent(), message.getContent());
            DummyContent.ITEMS.add(item);
            DummyContent.ITEM_MAP.put(j + "", item);
            j++;
        }
        adapter.notifyDataSetChanged();

    }
    private String convertDateToString(DateTime d){
        return "Oct 12 12:50 AM";
    }
}
