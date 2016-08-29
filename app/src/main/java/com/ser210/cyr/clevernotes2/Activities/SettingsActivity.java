/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This is the settings activity that allows the user to change different aspects of the app
 */

package com.ser210.cyr.clevernotes2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ser210.cyr.clevernotes2.Activities.Intro.WelcomeActivity;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes_sdk21.R;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;

    //This is the onCreate method that is called when the activity is first started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        listView = (ListView) findViewById(R.id.settings_list_view);

        if (listView != null) {
            listView.setOnItemClickListener(this);
        }
    }

    //Called when an item in the listview is clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                break;
            case 1:
                deleteName();
                break;
            case 2:
                startAboutActivity();
                break;
            case 3:
                deleteAllData(view);
                break;
            case 4:
                exportData();
                break;
        }
    }

    //Called when the delete button is clicked
    private void deleteName() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this, Constants.USER_DATA_TABLE, null, 1);
        databaseHelper.deleteUsername();

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    //Delete all data from database tables
    private void deleteAllData(View view) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this, Constants.NOTES_TABLE, null, 1);
        databaseHelper.deleteAllData();

        databaseHelper = new DatabaseHelper(this, Constants.USER_DATA_TABLE, null, 1);
        databaseHelper.deleteUsername();

        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    private void startAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void exportData() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this, Constants.NOTES_TABLE, null, 1);
        String data = databaseHelper.getAllNotesToExport();

        Intent intent = new Intent();
        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, data);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void startThemeActivity() {
        //Put theme activity code here
    }

}

