package com.ser210.cyr.clevernotes2.Activities.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ser210.cyr.clevernotes2.Activities.MainActivity;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes_sdk21.R;

public class WelcomeActivity extends AppCompatActivity {

    EditText editText;
    String name;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        hideActionBar();

        editText = (EditText) findViewById(R.id.name_field);
        databaseHelper = new DatabaseHelper(this, Constants.USER_DATA_TABLE, null, 1);

        if (databaseHelper.containsUsername()) {
            startMainActivity();
        }
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }

    public void onClickStart(View view) {

        String input = editText.getText().toString();

        if (input.length() > 0) {
            name = input;
            databaseHelper.insertUsername(name);
            startMainActivity();
        } else {
            Toast.makeText(WelcomeActivity.this, "Please enter your name", Toast.LENGTH_LONG).show();
        }
    }

}
