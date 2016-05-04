package com.thyn.broadcast;


import android.os.AsyncTask;
import com.thyn.android.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.content.Context;
import java.io.IOException;
import com.thyn.connection.GoogleAPIConnector;

import android.widget.Toast;
import java.util.logging.Logger;
import java.util.logging.Level;


public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, Void> {
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
    private static final String SENDER_ID = "953474522001";

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        /*if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                    // otherwise they can be skipped
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end of optional local run code

            regService = builder.build();
        }*/
        try {
            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            GoogleAPIConnector.connect_DeviceRegistrationAPI().register(SENDER_ID).execute();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }
}


