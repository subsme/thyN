package com.thyn.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
public class Signup1Activity extends AppCompatActivity {
    private static final String TAG = "Signup1Activity";
    SignupLooperThread<ProgressDialog> signupLooperThread;
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
        signupLooperThread = new SignupLooperThread<ProgressDialog>(new Handler());
        signupLooperThread.setListener(new SignupLooperThread.Listener<ProgressDialog>() {
            public void onResponseFromServer(ProgressDialog progressDialog, APIGeneralResult rslt) {
                Log.i(TAG, "APIGeneralResult a is " + rslt.getMessage());
                if (rslt != null && Long.parseLong(rslt.getStatusCode()) > 0) {
                    //MyServerSettings.initializeUserProfile(getApplicationContext(), rslt.getStatusCode(), rslt.getMessage(), null, null);
                    onSignupSuccess();
                }
                else onSignupFailed();
                progressDialog.dismiss();

            }
        });
        signupLooperThread.start();
        signupLooperThread.getLooper();
        Log.i(TAG, "Background thread started");
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Signup1Activity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        signupLooperThread.queueRequest(progressDialog, email, password, name);



        /*new android.os.Handler().postDelayed(
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
                }, 3000);*/
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

}