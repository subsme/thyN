package com.thyn.intro;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.thyn.common.MyServerSettings;
import com.thyn.connection.AppStatus;
import com.thyn.navigate.NavigationActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context c = getBaseContext();
        /*
        Subu - Important to disable this when no need to debug local logs.
         */
        MyServerSettings.writeLocalLogs(c);

        String token = MyServerSettings.getUserSocialId(c);
        int type = MyServerSettings.getUserSocialType(c);

        if(token != null &&
                type != -1) {
            Intent intent = new Intent(getApplicationContext(), LoginSplash.class);
            startActivity(intent);
            finish();
            return;
        }
        //setContentView(R.layout.activity_splash); No time for setting content view.
        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(intent);
        finish();
    }
}
