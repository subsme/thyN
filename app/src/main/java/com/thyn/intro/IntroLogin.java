package com.thyn.intro;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thyn.R;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.android.backend.userAPI.model.APIUserInformation;
import com.thyn.android.backend.userAPI.model.ExternalLogonPackage;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.connection.ReceiveStatsFromServerAsyncTask;
import com.thyn.deleteMeInProd.AndroidDatabaseManager;
import com.thyn.tab.WelcomePageActivity;
import android.content.Intent;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by shalu on 7/13/16.
 */
public class IntroLogin extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    //private ImageView buttonGoogleLogin;
    private LoginButton fbloginButton;
    CallbackManager callbackManager;
    private static final String TAG = "IntroLogin";
    private static final int RC_SIGN_IN = 9001;
    private TextView mStatusTextView;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);


        // Make sure you initialize Facebooksdk before setting content view. Otherwise it wont recognize com.facebook.login.widget.LoginButton.
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_intro_login);
        Button button =(Button)findViewById(R.id.thyn_sign_in_button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });
       // buttonGoogleLogin = (ImageView)findViewById(R.id.sign_in_button);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id)) /* Important. Otherwise, I wont get token from the server */
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]

        /*buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                Intent intent = new Intent(getApplicationContext(), WelcomePageActivity.class);
                startActivity(intent);
            }
        });*/

        callbackManager = CallbackManager.Factory.create();
        fbloginButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbloginButton.setReadPermissions("email");
        // Callback registration
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                AccessToken token = loginResult.getAccessToken();
                final String fbToken = token.getToken();
                Log.d(TAG, "FB Access token is" + fbToken);
                final String userid;
                GraphRequest request = GraphRequest.newMeRequest(
                        token,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response){
                                // Application code
                                Log.d(TAG, "The id is : " + object.optString("id"));
                                Log.d(TAG, "The name is : " + object.optString("name"));
                                try {
                                    JSONObject picturedata = object.getJSONObject("picture");
                                    JSONObject pdata = picturedata.getJSONObject("data");
                                    String imageurl = pdata.getString("url");
                                    Log.d(TAG, "The link is : " + imageurl);
                                    MyServerSettings.initializeUserProfile(getApplicationContext()
                                            , 0 // 0 means facebook. 1 means google.
                                            , object.optString("id")
                                            , object.optString("name")
                                            , fbToken
                                            , imageurl);
                                }
                                catch (JSONException je){
                                    Log.e(TAG,je.getMessage());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,picture");
                request.setParameters(parameters);
                request.executeAsync();
                /*
                We have authenticated with facebook. Now we are sending the token to the server.
                After sending the token to the server, we are starting a new activity - WelcomePageActivity.
                 */

                new SendToServerAsyncTask().execute(fbToken, "Facebook");


            }

            @Override
            public void onCancel() {
                // App code

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "FB exception");
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else{
            Log.d(TAG,"FB callbackmanager called.");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Log.d(TAG, "sign in successful");
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.d(TAG, "Signed in by :"
                    + acct.getDisplayName()
                    + ", acct id :"
                    + acct.getId()
                    + ", acct token id :"
                    + acct.getIdToken());


            MyServerSettings.initializeUserProfile(getApplicationContext()
                    , 1 // 0 means facebook and 1 means google.
                    , acct.getId()
                    , acct.getDisplayName()
                    , acct.getIdToken()
                    , acct.getPhotoUrl().toString());

            //updateUI(true);
            new SendToServerAsyncTask().execute(acct.getIdToken(),  "Google");

            Intent intent = new Intent(getApplicationContext(), WelcomePageActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "sign in failure");
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
        }
    }
    private class SendToServerAsyncTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {


            String token = params[0].toString();
            String loginType = params[1].toString();

            Log.d(TAG,"Login type: " + loginType);
            Log.d(TAG,"token: " + token);

            ExternalLogonPackage logPack = new ExternalLogonPackage();
            logPack.setAccessToken(token);
            APIUserInformation rslt = null;

                   try {
                       if(loginType.equalsIgnoreCase("Google")) {
                           rslt = GoogleAPIConnector.connect_UserAPI().logonWithGoogle(logPack).execute();
                       }
                       else if(loginType.equalsIgnoreCase("Facebook")){
                           rslt = GoogleAPIConnector.connect_UserAPI().logonWithFacebook(logPack).execute();
                       }

                       if (rslt != null && rslt.getResult().getStatusCode() != null) {

                           String message = rslt.getResult().getMessage();
                           Log.d(TAG, "78The message from the server is: " + message);
                           Log.d(TAG, "The user profile id is: " + rslt.getProfileID());
                           MyServerSettings.initializeUserProfileID(getApplicationContext(), rslt.getProfileID());
                           Log.d(TAG, "Profile ID set: " + MyServerSettings.getUserProfileId(getApplicationContext()));
                           Log.d(TAG, "Neighbors helped" + rslt.getNumNeighbrsHelped());
                           Log.d(TAG, "points gathered " + rslt.getThyNPoints());
                           MyServerSettings.initializeNumNeighbrsIHelped(getApplicationContext(), rslt.getNumNeighbrsHelped());
                           MyServerSettings.initializePoints(getApplicationContext(), rslt.getThyNPoints());
                       }
                   }
                   catch(Exception e){
                       e.printStackTrace();
                   }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            MyServerSettings.initializeEnvironment(getBaseContext());
            Intent intent = new Intent(getApplicationContext(), WelcomePageActivity.class);
            startActivity(intent);

        }
    }

}
