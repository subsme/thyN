package com.thyn.intro;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.thyn.R;
import com.thyn.tab.WelcomePageActivity;
import android.content.Intent;
/**
 * Created by shalu on 7/13/16.
 */
public class IntroLogin extends AppCompatActivity {
    private ImageView buttonGoogleLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_login);
        buttonGoogleLogin = (ImageView)findViewById(R.id.google_login);
        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WelcomePageActivity.class);
                startActivity(intent);
            }
        });
    }
}
