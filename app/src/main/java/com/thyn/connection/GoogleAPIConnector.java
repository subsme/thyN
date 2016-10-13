package com.thyn.connection;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.thyn.android.backend.userAPI.UserAPI;
import com.thyn.android.backend.myTaskApi.MyTaskApi;
import com.thyn.android.backend.registration.Registration;
import com.google.api.client.http.HttpHeaders;

import java.io.IOException;
import android.util.Log;

import com.google.api.client.googleapis.services.AbstractGoogleClient;

/**
 * Created by shalu on 3/10/16.
 */
public class GoogleAPIConnector {
    private static String TAG = "GoogleAPIConnector";
    //xz-logical-bird-e.appspot.com
    private static String LOCAL_APP_ENGINE_SERVER_URL_FOR_ANDROID= "http://10.0.2.2:8080";
    private static String REMOTE_APP_ENGINE_SERVER_URL_FOR_ANDROID= "https://xz-logical-bird-e.appspot.com";

    private static boolean LOCAL_ANDROID_RUN = false;

    private static String appName = "xz-logical-bird-e";
    public static UserAPI myApiService = null;
    public static MyTaskApi myTaskAPIService = null;
    public static Registration myDeviceRegistrationService = null;
    static HttpHeaders APIHeaders = null;
    public static UserAPI connect_UserAPI() {
            if(myApiService == null) {
                UserAPI.Builder builder = new UserAPI.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null);
                if(!LOCAL_ANDROID_RUN) {
                    Log.d(TAG, "Connecting to " + REMOTE_APP_ENGINE_SERVER_URL_FOR_ANDROID);
                    builder.setRootUrl(REMOTE_APP_ENGINE_SERVER_URL_FOR_ANDROID + "/_ah/api/");
                }

                builder = updateBuilder(builder);
                myApiService = builder.build();
            }
            else{
                Log.d(TAG, "reusing myUserAPIService which was built earlier");
            }

        return myApiService;
    }
   /* public static UserAPI connect_UserAPI() {
        UserAPI myApiService = null;
        UserAPI.Builder builder = new UserAPI.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl(LOCAL_APP_ENGINE_SERVER_URL_FOR_ANDROID
                        + "/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        // end options for devappserver

        myApiService = builder.build();

        return myApiService;
    }*/
    public static MyTaskApi connect_TaskAPI() {
         if(myTaskAPIService == null) {
            Log.d(TAG, "Have to build myTaskAPIService freshly");
            MyTaskApi.Builder builder = new MyTaskApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver

             // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
             // otherwise they can be skipped
             if(!LOCAL_ANDROID_RUN) {
                 Log.d(TAG, "Connecting to " + REMOTE_APP_ENGINE_SERVER_URL_FOR_ANDROID);
                 builder.setRootUrl(REMOTE_APP_ENGINE_SERVER_URL_FOR_ANDROID + "/_ah/api/");
             }

             builder = updateBuilder(builder);
             myTaskAPIService = builder.build();

        }else{
             Log.d(TAG, "reusing myTaskAPIService which was built earlier");
         }

        return myTaskAPIService;
    }
    public static Registration connect_DeviceRegistrationAPI(){
            if (myDeviceRegistrationService == null) {
             Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);
                    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                    // otherwise they can be skipped
             if(!LOCAL_ANDROID_RUN) {
                 Log.d(TAG, "Connecting to " + REMOTE_APP_ENGINE_SERVER_URL_FOR_ANDROID);
                 builder.setRootUrl(REMOTE_APP_ENGINE_SERVER_URL_FOR_ANDROID + "/_ah/api/");
             }

                builder = updateBuilder(builder);
                myDeviceRegistrationService = builder.build();
        }else{
                Log.d(TAG, "reusing myDeivceRegistrationService which was built earlier");
            }
        return myDeviceRegistrationService;
    }
    /**
     * Updates the Google client builder to connect the appropriate server based
     * on whether LOCAL_ANDROID_RUN is true or false.
     *
     * @param builder
     *            Google client builder
     * @return same Google client builder
     */
    public static <B extends AbstractGoogleClient.Builder> B updateBuilder(
            B builder) {

        builder.setApplicationName(appName);

        if (LOCAL_ANDROID_RUN) {
            Log.d(TAG, "Connecting to " + LOCAL_APP_ENGINE_SERVER_URL_FOR_ANDROID);
            builder.setRootUrl(LOCAL_APP_ENGINE_SERVER_URL_FOR_ANDROID
                    + "/_ah/api/");
        }

        // only enable GZip when connecting to remote server
        final boolean enableGZip = builder.getRootUrl().startsWith("https:");

        builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
            public void initialize(AbstractGoogleClientRequest<?> request)
                    throws IOException {
                if (!enableGZip) {
                    request.setDisableGZipContent(true);
                }
                APIHeaders = request.getLastResponseHeaders();
            }
        });
        if(APIHeaders != null) {
            Log.d(TAG, "Cookie: " + APIHeaders.getCookie());
            Log.d(TAG, "Headers: " + APIHeaders.toString());
        }

        return builder;
    }
    public static boolean isLocalAndroidRun() {
        return LOCAL_ANDROID_RUN;
    }

    public static void setLocalAndroidRun(boolean localAndroidRun) {
        LOCAL_ANDROID_RUN = localAndroidRun;
    }

}
