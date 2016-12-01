package com.thyn.intro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thyn.android.backend.userAPI.model.APIUserInformation;
import com.thyn.android.backend.userAPI.model.ExternalLogonPackage;
import com.thyn.broadcast.GCMPreferences;
import com.thyn.broadcast.GcmRegistrationIntentService;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.AppStatus;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.navigate.NavigationActivity;

public class LoginSplash extends AppCompatActivity {
    private static final String TAG = "LoginSplash";
    private ProgressDialog dialog;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    private String token;
    private String loginType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_splash); No time for setting content view.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("TOKEN");
            loginType = extras.getString("LOGIN_TYPE");
            //The key argument here must match that used in the other activity
            new SendToServerAsyncTask(this).execute(token, loginType);

        }
        else{ //Subu: I will come here if its not the first time for the user. The user has already signed in previously (maybe yesterday) and i use the android cache to sign the user in again.
            Context c = getBaseContext();
            token = MyServerSettings.getUserSocialId(c);
            int type = MyServerSettings.getUserSocialType(c);
            if(token != null &&
                     type != -1){
                if(type == 0) loginType = "Facebook";
                else loginType = "Google";
                new SendToServerAsyncTask(this).execute(token, loginType);
                return;
            }
            Log.d(TAG, "Coming from BasicProfileActivity...starting NavigationActivity...waiting for 3 seconds so that the WelcomeActivity's tasklists load immediately");
            /*new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 3000);*/
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            startActivity(intent);
            finish();
        }

    }
    private class SendToServerAsyncTask extends AsyncTask<String, Void, Void> {
        private Activity activity;
        private boolean shouldWeShowBasicProfileScreen = false;

        public SendToServerAsyncTask(Activity activity) {
            this.activity = activity;
            shouldWeShowBasicProfileScreen = false;
        }

        @Override
        protected Void doInBackground(String... params) {


            String token = params[0].toString();
            String loginType = params[1].toString();

            Log.d(TAG, "Login type: " + loginType);
            Log.d(TAG, "token: " + token);

            ExternalLogonPackage logPack = new ExternalLogonPackage();
            logPack.setAccessToken(token);
            APIUserInformation rslt = null;

            try {
                if (loginType.equalsIgnoreCase("Google")) {
                    rslt = GoogleAPIConnector.connect_UserAPI().logonWithGoogle(logPack).execute();
                } else if (loginType.equalsIgnoreCase("Facebook")) {
                    rslt = GoogleAPIConnector.connect_UserAPI().logonWithFacebook(logPack).execute();
                }

                if (rslt != null && rslt.getResult().getStatusCode() != null) {

                    String message = rslt.getResult().getMessage();
                    Log.d(TAG, "78The message from the server is: " + message);
                    Log.d(TAG, "The user profile id is: " + rslt.getProfileID());
                    MyServerSettings.initializeUserProfileID(getApplicationContext(), rslt.getProfileID());
                    MyServerSettings.initializeUserName(getApplicationContext(), rslt.getName());
                    Log.d(TAG, "Profile ID set: " + MyServerSettings.getUserProfileId(getApplicationContext()));
                    Log.d(TAG, "Neighbors helped" + rslt.getNumNeighbrsHelped());
                    Log.d(TAG, "points gathered " + rslt.getThyNPoints());
                    MyServerSettings.initializeNumNeighbrsIHelped(getApplicationContext(), rslt.getNumNeighbrsHelped());
                    MyServerSettings.initializePoints(getApplicationContext(), rslt.getThyNPoints());
                    MyServerSettings.initializeUserAddress(getApplicationContext(), rslt.getAddress(), rslt.getCity(), Double.toString(rslt.getLatitude()), Double.toString(rslt.getLongitude()));
                    if (!rslt.getBasicprofileInfo()) {
                        Log.d(TAG, "We dont have profile information(phone, address) for the user");
                        shouldWeShowBasicProfileScreen = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
          /*  dialog = new ProgressDialog(activity);
            dialog.setMessage("Please wait");
            dialog.show();*/
        }

        @Override
        protected void onPostExecute(Void result) {
            Context c = getBaseContext();
            MyServerSettings.initializeEnvironment(c);
/*
            Intent intent = null;
            if (!this.shouldWeShowBasicProfileScreen) {
                intent = new Intent(getApplicationContext(), WelcomePageActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), BasicProfileActivity.class);
            }
            startActivity(intent);
*/
            if(!this.shouldWeShowBasicProfileScreen) MyServerSettings.startPolling(c);

            GCMInitiation(this.shouldWeShowBasicProfileScreen);
            //dismissProgressDialog();
        }



  /*      protected void dismissProgressDialog() {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

        }*/
    }
    private void GCMInitiation(final boolean b){
        Log.d(TAG, "GCMInitiation()");

        //mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    //mInformationTextView.setText(getString(R.string.gcm_send_message));
                    Log.d(TAG, "The token has already been sent to the server. Starting MyTaskListActivity.class");

                    //Intent intent1 = null;
                    if (!b) {
                        Log.d(TAG, "starting NavigationActivity...");
                        intent = new Intent(getApplicationContext(), NavigationActivity.class);
                    } else {
                        Log.d(TAG, "starting BasicProfileActivity...");
                        intent = new Intent(getApplicationContext(), BasicProfileActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    //mInformationTextView.setText(getString(R.string.token_error_message));
                    Log.d(TAG, "don't have the token in the SharedPreferences cache. Need to ask GCM server for a token that I can send to the app server");
                }
            }
        };
        //mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.d(TAG, "checkPlayServices() is successful. Starting GcmRegistrationIntentService.class");
            Intent intent = new Intent(this, GcmRegistrationIntentService.class);
            startService(intent);
        }


    }
    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
        Log.d(TAG, "Registered the receiver");
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
