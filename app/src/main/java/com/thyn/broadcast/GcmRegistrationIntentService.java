package com.thyn.broadcast;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.util.logging.Logger;
import java.util.logging.Level;
import android.os.Looper;
import java.util.logging.Handler;
import android.widget.Toast;
import com.google.android.gms.iid.InstanceID;
import android.os.AsyncTask;
import com.thyn.android.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.content.Context;
import java.io.IOException;
import java.net.URLEncoder;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gms.gcm.GcmPubSub;
import android.support.v4.content.LocalBroadcastManager;
import com.thyn.R;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.user.LoginActivity;

/**
 * Created by shalu on 3/31/16.
 */
public class GcmRegistrationIntentService extends IntentService{
    private static final String TAG = "GcmRegistratioIntentService";
    private static final String[] TOPICS = {"global"};

    public GcmRegistrationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try{
        // [START register_for_gcm]
        // Initially this call goes out to the network to retrieve the token, subsequent calls
        // are local.
        // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
        // See https://developers.google.com/cloud-messaging/android/start for details on this file.
        // [START get_token]
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        // [END get_token]
        Log.i(TAG, "GCM Registration Token: " + token);
        Long userprofileid = sharedPreferences.getLong(MyServerSettings.PREF_USERPROFILE_ID,-1);
            Log.d(TAG, "Sending user profile id:" + userprofileid);

        // TODO: Implement this method to send any registration to your app's servers.
            // https://developers.google.com/cloud-messaging/registration - if the GCM is used exclusively
            // for topic messaging, you do not need to pass the registration token to the app server.
        sendRegistrationToServer(token,userprofileid);

        // Subscribe to topic channels
        subscribeTopics(token);

        // You should store a boolean that indicates whether the generated token has been
        // sent to your server. If the boolean is false, send the token to your server,
        // otherwise your server should have already received the token.
        sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, true).apply();
        // [END register_for_gcm]
    } catch (Exception e) {
        Log.d(TAG, "Failed to complete token refresh", e);
        // If an exception happens while fetching the new token or updating our registration data
        // on a third-party server, this ensures that we'll attempt the update at a later time.
        sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false).apply();
    }
    // Notify UI that registration has completed, so the progress indicator can be hidden.
    Intent registrationComplete = new Intent(GCMPreferences.REGISTRATION_COMPLETE);
    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
}

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, Long profile_id){
        // Add custom implementation, as needed.
        // https://developers.google.com/cloud-messaging/registration -
        // if the GCM is used exclusively
        // for topic messaging, you do not need to pass the registration token to the app server.
        // Thats why doesnt have any implementation because i am just doing topic messaging.
        try {

            Log.d(TAG,"Sending the registration token to the app server:" + token);
            token = URLEncoder.encode(token,"UTF-8");
            APIGeneralResult res = GoogleAPIConnector.connect_UserAPI().setGCMRegistrationToken(profile_id, token.trim()).execute();
            Log.d(TAG, "API Call response: setGCMRegistrationToken()" + res.getMessage());
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}