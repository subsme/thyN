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
    private static final String TAG = "GcmRegistrationIntentService";

    public static final String KEY = "key";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";
    public static final String TOPIC = "topic";

    private static final String[] TOPICS = {"global"};

    public GcmRegistrationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = null;
        String key = intent.getStringExtra(KEY);
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
        Log.i(TAG, "GCM Registration Token: " + token);
        // [END get_token]

        if(key!= null && key.equalsIgnoreCase(this.SUBSCRIBE)){
            String topic = intent.getStringExtra(TOPIC);
            // Subscribe to topic channels
            subscribeToTopic(token, topic);
        }
        else if(key != null && key.equalsIgnoreCase(this.UNSUBSCRIBE)){
                String topic = intent.getStringExtra(TOPIC);
                // UnSubscribe to topic channels
                unSubscribeToTopic(token, topic);
            }
        else {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Long userprofileid = sharedPreferences.getLong(MyServerSettings.PREF_USERPROFILE_ID, -1);
            Log.d(TAG, "Sending user profile id:" + userprofileid);
            // TODO: Implement this method to send any registration to your app's servers.
            // https://developers.google.com/cloud-messaging/registration - if the GCM is used exclusively
            // for topic messaging, you do not need to pass the registration token to the app server.
            boolean b = sendRegistrationToServer(token, userprofileid);
            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            if(!b){
                Log.d(TAG, "Failed sending registration token to server. Resending again...");
                b = sendRegistrationToServer(token, userprofileid);
            }
            sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, b).apply();
        }


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
    private boolean sendRegistrationToServer(String token, Long profile_id){
        // Add custom implementation, as needed.
        // https://developers.google.com/cloud-messaging/registration -
        // if the GCM is used exclusively
        // for topic messaging, you do not need to pass the registration token to the app server.
        // Thats why doesnt have any implementation because i am just doing topic messaging.
        try {

            Log.d(TAG,"Sending the registration token to the app server:" + token);
            token = URLEncoder.encode(token,"UTF-8");
            APIGeneralResult res = GoogleAPIConnector.connect_UserAPI().setGCMRegistrationToken(profile_id, token.trim()).execute();
            Log.d(TAG, "API Call response: setGCMRegistrationToken() - Code: " + res.getMessage());
            if(res.getCode() == 0) return false;
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
        return true;
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeToTopic(String token, String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        if(token != null && topic != null) {
            pubSub.subscribe(token, "/topics/topic_thyN_" + topic, null);
            Log.d(TAG, "Subscribed to topic: topic_thyN_" + topic);
        }
        else{
            Log.d(TAG, "Subscribing to topic failed");
            if(token == null) Log.e(TAG, "Token is null");
            if(topic == null) Log.e(TAG, "Topic is null");
        }
        //for (String topic : TOPICS) {
         //   pubSub.subscribe(token, "/topics/" + topic, null);
        //}
    }
    // [END subscribe_topics]
    private void unSubscribeToTopic(String token, String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        if(token != null && topic != null) {
            pubSub.unsubscribe(token, "/topics/topic_thyN_" + topic);
            Log.d(TAG, "Unsubscribed from topic: topic_thyN_" + topic);
        }
        else{
            Log.d(TAG, "Unsubscribed from topic failed");
            if(token == null) Log.e(TAG, "Token is null");
            if(topic == null) Log.e(TAG, "Topic is null");
        }
    }

}