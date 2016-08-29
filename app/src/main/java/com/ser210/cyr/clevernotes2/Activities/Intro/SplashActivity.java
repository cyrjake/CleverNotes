/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This Activity displays a splash screen when the app is first started containing
 * the app icon and the name of the app. The Handler starts the Main Activity after the specified time
 * has passed.
 */

package com.ser210.cyr.clevernotes2.Activities.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.ser210.cyr.clevernotes2.Activities.MainActivity;
import com.ser210.cyr.clevernotes_sdk21.R;

public class SplashActivity extends AppCompatActivity {

    //Length of time to display Splash Screen
    private final int SPLASH_DISPLAY_LENGTH = 2500;

    //This method is called when the Activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Hide the Action Bar
        hideActionBar();

        //Create Handler to wait specified time before starting main activity
        startHandler();
    }

    //Start the Handler with the set time and start the Main Activity after the time has passed
    private void startHandler(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    //Hide the Action Bar
    private void hideActionBar() {
        //Hide the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
