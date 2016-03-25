package photogallery.android.bignerdranch.com.thyn.user;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thyn.backend.userAPI.UserAPI;
import com.thyn.backend.userAPI.model.APIGeneralResult;
import com.thyn.backend.userAPI.model.ThyNLogonPackage;
import com.thyn.backend.userAPI.model.Profile;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import photogallery.android.bignerdranch.com.thyn.R;
import photogallery.android.bignerdranch.com.thyn.tab.MyTaskListActivity;
import photogallery.android.bignerdranch.com.thyn.connection.GoogleAPIConnector;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    APIGeneralResult rslt = null;
    Profile prof = null;
    public static final String PREF_USERPROFILE_ID = "userProfileID";
    public static final String PREF_USERPROFILE_NAME = "userProfileName";


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
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }
    public void storeUserProfile(){

        try {
            prof = GoogleAPIConnector.connect_UserAPI().getCurrentUserProfile().execute();
            if (prof == null) {
                Log.d(TAG, "Profile object is null");
            } else {
                Log.d(TAG, "Retrieved profile object.");

                String fname = prof.getFirstName();
                String lname = prof.getLastName()!=null?prof.getLastName().substring(0,1).toUpperCase():null;
                fname = fname + lname!=null?" " + lname+".":"";
                Log.d(TAG, "Name extracted is: " + fname + " " + lname+".");
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putLong(LoginActivity.PREF_USERPROFILE_ID,prof.getId())
                        .putString(LoginActivity.PREF_USERPROFILE_NAME,fname)
                        .commit();
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        new SendToServerAsyncTask().execute(email,password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if( rslt != null && rslt.getStatusCode().equalsIgnoreCase("OK")) {
                            onLoginSuccess();
                            //Intent i = new Intent(getBaseContext(), TaskListActivity.class);
                            Intent i = new Intent(getBaseContext(), MyTaskListActivity.class);
                            startActivity(i);

                        }
                        else
                            onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
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
    private static UserAPI myApiService = null;

    private class SendToServerAsyncTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {

            if(prof == null) {
                try {

                    String email = params[0].toString();
                    String password = params[1].toString();
                    Log.d(TAG, email);
                    Log.d(TAG, password);
                    //a.setTaskDescription("take off homie 2");
                    //return myApiService.replay(2,a).execute().getTaskDescription();
                    ThyNLogonPackage tnlp = new ThyNLogonPackage();
                    tnlp.setEmail(email);
                    tnlp.setPassword(password);
                    //rslt = myApiService.logonWithThyN(tnlp).execute();
                    rslt = GoogleAPIConnector.connect_UserAPI().logonWithThyN(tnlp).execute();
                    if (rslt != null) {
                        //Log.d(TAG, rslt.getMessage());
                        storeUserProfile();
                    }


                } catch (IOException e) {
                    e.getMessage();
                }
            }
            return null;
        }



    }
}
