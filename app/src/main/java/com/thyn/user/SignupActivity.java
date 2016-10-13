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

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
//import com.android.thyn.backend.myTaskApi.MyTaskApi;
//import com.android.thyn.backend.myTaskApi.model.MyTask;
import com.thyn.android.backend.userAPI.UserAPI;
import com.thyn.android.backend.userAPI.model.APIGeneralResult;
import com.thyn.android.backend.userAPI.model.ThyNRegisterPackage2;


import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.thyn.R;

import com.thyn.broadcast.MainActivity;
import com.thyn.common.MyServerSettings;
import com.thyn.connection.GoogleAPIConnector;
import com.thyn.tab.WelcomePageActivity;
import android.os.HandlerThread;
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    APIGeneralResult rslt = null;

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        new SendToServerAsyncTask().execute(name,email,password);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed 
                        // depending on success
                            Log.d(TAG, "The result object is: " +  rslt);
                        if(rslt != null){
                            Log.d(TAG, rslt.getMessage());
                            Log.d(TAG, rslt.getStatusCode());
                        }
                        if(rslt != null && Long.parseLong(rslt.getStatusCode()) > 0 ) {
                            onSignupSuccess();
                        }
                        else onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 10000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Log.d(TAG, "Calling GCM MainActivity from registration class");
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sorry! This email is already taken.", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "Background thread destroyed");
    }
    private static UserAPI myApiService = null;



    private class SendToServerAsyncTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {

            try {
                String name = params[0].toString();
                String email = params[1].toString();
                String password = params[2].toString();
                Log.d(TAG, name);
                Log.d(TAG, email);
                Log.d(TAG, password);
                ThyNRegisterPackage2 tnrp = new ThyNRegisterPackage2();
                tnrp.setEmail(email);
                tnrp.setName(name);
                tnrp.setPassword(password);
                rslt = GoogleAPIConnector.connect_UserAPI().registerNewThyNUser(tnrp).execute();
                if (rslt != null && rslt.getStatusCode() != null) {
                    Log.d(TAG, "Retrieved profile object.");
                    String fname = rslt.getMessage();
                    Log.d(TAG, "Name extracted is: " + fname );
                    //MyServerSettings.initializeUserProfile(getApplicationContext(), rslt.getStatusCode(), fname, null, null);
                }
            } catch (IOException e) {
                e.getMessage();
            }
            return null;
        }


    }
}