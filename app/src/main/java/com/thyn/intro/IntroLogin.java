package com.thyn.intro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thyn.R;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.android.backend.userAPI.model.APIUserInformation;
import com.thyn.android.backend.userAPI.model.ExternalLogonPackage;
import com.thyn.broadcast.GCMPreferences;
import com.thyn.broadcast.GcmRegistrationIntentService;
import com.thyn.broadcast.MainActivity;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.AppStatus;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.connection.ReceiveStatsFromServerAsyncTask;
import com.thyn.deleteMeInProd.AndroidDatabaseManager;
import com.thyn.navigate.NavigationActivity;
import com.thyn.tab.WelcomePageActivity;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

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

    private ProgressDialog dialog;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

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
                Log.d(TAG, "FB Access token is: " + fbToken);
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
                //Log.d(TAG, "Calling SendToServerAsyncTask for Facebook login");
                //new SendToServerAsyncTask(getParent()).execute(fbToken, "Facebook");
                Intent intent = new Intent(getApplicationContext(), LoginSplash.class);
                intent.putExtra("TOKEN", fbToken);
                intent.putExtra("LOGIN_TYPE", "Facebook");
                startActivity(intent);
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
    public void onPause() {
        super.onPause();

      /*  if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;*/
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
            Context c = getApplicationContext();
            if (!AppStatus.getInstance(c).isOnline()) {
                Toast.makeText(getApplicationContext(), "Sorry! There is no Internet connection", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "############Not online!!!!#########");
                return;
            }
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
            //new SendToServerAsyncTask(this).execute(acct.getIdToken(), "Google");
            /*new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 1000);*/
            Intent intent = new Intent(getApplicationContext(), LoginSplash.class);
            intent.putExtra("TOKEN", acct.getIdToken());
            intent.putExtra("LOGIN_TYPE", "Google");
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
        if (!AppStatus.getInstance(this).isOnline()) {
            Toast.makeText(getApplicationContext(), "Sorry! There is no Internet connection", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "############Not online!!!!#########");
            return;
        }
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
        }
    }

}
