/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This is the settings fragment that allows the user to change different aspects of the app
 */

package com.ser210.cyr.clevernotes2.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ser210.cyr.clevernotes2.Activities.Intro.WelcomeActivity;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes_sdk21.R;

public class SettingsFragment extends Fragment implements OnItemClickListener {

    String LOG_TITLE = "SettingsFrag";
    String LOG_CONTENT = "is working";
    ListView listView;
    NoteListFragment noteListFragment;
    AboutFragment aboutFragment;
    SettingsItemClickedListener settingsItemClickedListener;

    //Listener that alerts the main activity when a button is clicked
    public interface SettingsItemClickedListener {
        void onSettingsItemClicked(int position);
    }

    //Called when the fragment is first created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    //Called when the layout is finished setting up
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView)getActivity().findViewById(R.id.settings_list_view_frag);
        if (listView != null) {
            listView.setOnItemClickListener(this);
        }
        this.settingsItemClickedListener = (SettingsItemClickedListener)getActivity();
    }

    //Called when an item in the ListView is clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.i(LOG_TITLE, LOG_CONTENT);
        settingsItemClickedListener.onSettingsItemClicked(position);

    }

    //Delete the username and retrieves a new name
    public void deleteName() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), Constants.USER_DATA_TABLE, null, 1);
        databaseHelper.deleteUsername();

        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(intent);
    }

    //Delete all data from database tables
    public void deleteAllData(View view) {
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), Constants.NOTES_TABLE, null, 1);
        databaseHelper.deleteAllData();

        databaseHelper = new DatabaseHelper(getActivity(), Constants.USER_DATA_TABLE, null, 1);
        databaseHelper.deleteUsername();

        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        startActivity(intent);
    }

    //Export data as JSON
    public void exportData(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), Constants.NOTES_TABLE, null, 1);
        String data = databaseHelper.getAllNotesToExport();

        Intent intent = new Intent();
        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, data);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // declare a new frag2 instance and use the method the replace the right frame
    public void setupMinorFrames(Fragment frag2) {
        Log.i(LOG_TITLE, "SETUPMINORFRAMES is working");
        noteListFragment = new NoteListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.replace(R.id.frame_one, noteListFragment);
        ft.replace(R.id.frame_two, frag2);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

}
