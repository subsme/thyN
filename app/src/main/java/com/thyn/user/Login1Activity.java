package com.thyn.user;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thyn.R;
import com.thyn.android.backend.userAPI.UserAPI;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.android.backend.userAPI.model.Profile;
import com.thyn.android.backend.userAPI.model.ThyNLogonPackage;
import com.thyn.broadcast.MainActivity;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.os.HandlerThread;
import android.os.Handler;




public class Login1Activity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    APIGeneralResult rslt = null;
    Profile prof = null;
    LoginLooperThread<ProgressDialog> loginLooperThread;


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);


        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Signup1Activity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        /* initialize the environment and remove any cache */
        MyServerSettings.initializeEnvironment(getBaseContext());

        loginLooperThread = new LoginLooperThread<ProgressDialog>(new Handler());
        loginLooperThread.setListener(new LoginLooperThread.Listener<ProgressDialog>() {
            public void onResponseFromServer(ProgressDialog progressDialog, APIGeneralResult rslt) {
                Log.i(TAG, "APIGeneralResult a is " + rslt.getMessage());
                //Log.i(TAG, "Boolean b is " + b);
                if (rslt != null && Long.parseLong(rslt.getStatusCode()) > 0) {
                    MyServerSettings.initializeUserProfile(getApplicationContext(), Long.parseLong(rslt.getStatusCode()), rslt.getMessage());
                    onLoginSuccess();
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                } else onLoginFailed();
                progressDialog.dismiss();

            }
        });
        loginLooperThread.start();
        loginLooperThread.getLooper();
        Log.i(TAG, "Background thread started");

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        loginLooperThread.clearQueue();
        loginLooperThread.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login1Activity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        //new SendToServerAsyncTask().execute(email,password);



        loginLooperThread.queueRequest(progressDialog, email, password);

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        // if( rslt != null && rslt.getStatusCode().equalsIgnoreCase("OK")) {
                        if (rslt != null && Long.parseLong(rslt.getStatusCode()) > 0) {
                            onLoginSuccess();

                            //Intent i = new Intent(getBaseContext(), MyTaskListActivity.class);
                            //startActivity(i);
                            Intent i = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(i);

                        } else
                            onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Someone called us");
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically


                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }



}
