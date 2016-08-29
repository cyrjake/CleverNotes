/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This is the about activity that displays information about the
 * developers and version of the app.
 */


package com.ser210.cyr.clevernotes2.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ser210.cyr.clevernotes_sdk21.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
}
