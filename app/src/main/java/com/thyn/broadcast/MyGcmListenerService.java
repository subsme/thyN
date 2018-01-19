/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thyn.broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.thyn.R;
import com.thyn.navigate.NavigationActivity;
import com.thyn.tab.DashboardActivity;
import com.thyn.task.view.chat.ChatRoomFragment;
import com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyActivity;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message      = data.getString("message");
        String sender       = data.getString("sender");
        String profileID    = data.getString("profileID");
        String taskID       = data.getString("taskID");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message + ", Sender: " + sender + ", profileID: " + profileID);


        if (from.startsWith("/topics/")) {
            // message received from some topic.
            Log.d(TAG, "Got a message for topic: " + from);
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(sender, profileID, message, taskID);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String sender, String profileID, String message, String taskID) {
        //Intent intent = new Intent(this, MainActivity.class);
        //Intent intent = new Intent(this, TaskIWillHelpPagerViewOnlyActivity.class);
        /* 11/02 Don't use MainActivity.class or TaskIWillHelpPagerViewOnlyActivity.class. It will crash with this exception
        java.lang.IllegalArgumentException: No view found for id 0x1020002 (android:id/content). This is because in TaskListFragment,
        there is this code ->
        DashboardFragment dashboardFragment = new DashboardFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.navigation_fragment_container,
                            dashboardFragment,
                            dashboardFragment.getTag()).addToBackStack(null).commit();
            This code requires that fragment that needs to be replaced is R.id.navigation_fragment_container.
            This code got fixed when I added the line "Intent intent = new Intent(this, NavigationActivity.class)
         */
        Intent intent = new Intent(this, NavigationActivity.class);
        //intent.putExtra("broadcast","comingFromBroadcast");
        //intent.putExtra("message", message);
        //intent.putExtra("sender", sender);
        //intent.putExtra("profileID", profileID);
        //intent.putExtra("taskID", taskID);
        //intent.putExtra(com.thyn.task.view.iwillhelp.TaskIWillHelpPagerViewOnlyFragment.EXTRA_TASK_ID, taskID);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */
            , intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("thyNeighbr")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        //updateChatRoomFragment(sender, profileID, message);
    }
    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    public void updateChatRoomFragment(String sender, String profileID, String message) {

            Intent intent = new Intent("unique_chat_name");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        intent.putExtra("sender", sender);
        intent.putExtra("profileID", profileID);

        Context context = this.getApplicationContext();
        //send broadcast
        context.sendBroadcast(intent);
    }
}
