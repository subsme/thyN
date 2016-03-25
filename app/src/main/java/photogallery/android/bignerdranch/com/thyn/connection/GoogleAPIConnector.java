package photogallery.android.bignerdranch.com.thyn.connection;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.thyn.backend.userAPI.UserAPI;
import com.thyn.backend.myTaskApi.MyTaskApi;

import java.io.IOException;

/**
 * Created by shalu on 3/10/16.
 */
public class GoogleAPIConnector {
    private static String host= "http://10.0.2.2:8080/_ah/api/";
   // private static UserAPI myApiService = null;

    public static UserAPI connect_UserAPI() {
            UserAPI myApiService = null;
            UserAPI.Builder builder = new UserAPI.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl(host)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();

        return myApiService;
    }
    public static MyTaskApi connect_TaskAPI() {
        MyTaskApi myTaskAPIService= null;

        MyTaskApi.Builder builder = new MyTaskApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl(host)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        // end options for devappserver

        myTaskAPIService = builder.build();

        return myTaskAPIService;
    }


}
