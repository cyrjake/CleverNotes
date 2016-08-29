/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This is the main activity of the app. This activity displays all of the user's notes
 * ordered by the date modified.
 */

package com.ser210.cyr.clevernotes2.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ser210.cyr.clevernotes2.EntityClasses.Note;
import com.ser210.cyr.clevernotes2.Fragments.AboutFragment;
import com.ser210.cyr.clevernotes2.Fragments.NoteEditorFragment;
import com.ser210.cyr.clevernotes2.Fragments.NoteListFragment;
import com.ser210.cyr.clevernotes2.Fragments.SettingsFragment;
import com.ser210.cyr.clevernotes2.Fragments.TagsListFragment;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes2.HelperClasses.ScreenSize;
import com.ser210.cyr.clevernotes_sdk21.R;

public class MainActivity extends AppCompatActivity implements NoteListFragment.NoteClickListener,
        TagsListFragment.TagItemClickedListener, NoteEditorFragment.ViewsLoadedListener,
        SettingsFragment.SettingsItemClickedListener {

    DatabaseHelper databaseHelper;

    //Buttons change based on the screens visible
    boolean edit, share, delete, searchButton, lists, settings, save, spell, analyze;

    NoteListFragment noteList;
    NoteEditorFragment noteEditorFragment;
    TagsListFragment tagsListFragment;
    SettingsFragment settingsFragment;
    AboutFragment aboutFragment;

    String title, body, username;
    int id;

    //This method is called when the Activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNameTitle();
        init();
    }

    //Initialize the UI and decide whether to use fragments or activities
    private void init() {

        //Adds the fragment to this activity
        if (ScreenSize.isLargeScreen(this)) {
            setupLargeScreenMode();
        } else {
            startActivitiesForPhone();
        }
    }

    private void setupLargeScreenMode() {
        Log.v(Constants.LOG_TAG, "LARGE SCREEN");

        Intent intent = getIntent();
        int noteID = intent.getIntExtra("id", -1);
        boolean newNote = intent.getBooleanExtra("newNote", false);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        if (myFab != null) {
            myFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clearNoteData();
                    startEditor();
                    showNotesListOnLeft();
                }
            });
        }

        databaseHelper = new DatabaseHelper(this, Constants.NOTES_TABLE, null, 1);

        setAllFalse();
        searchButton = true;
        settings = true;

        noteEditorFragment = new NoteEditorFragment();
        noteList = new NoteListFragment();
        tagsListFragment = new TagsListFragment();
        settingsFragment = new SettingsFragment();
        aboutFragment = new AboutFragment();

        if (noteID != -1 || newNote) {
            checkIfNewNote(noteID);
        } else {
            setupFrames();
        }
    }

    private void checkIfNewNote(int noteID) {
        if (noteID != -1) {
            Note note = databaseHelper.getNote(noteID);
            id = noteID;
            title = note.getTitle();
            body = note.getBody();
            showNotesListOnLeft();
            startEditor();
        } else {
            id = -1;
            title = null;
            body = null;
            showNotesListOnLeft();
            startEditor();
        }
    }

    private void startActivitiesForPhone() {
        Log.v(Constants.LOG_TAG, "SMALL SCREEN");
        Intent intent = new Intent(this, NoteListActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }


    /*All methods below are for setting up and working with Fragments*/

    private void setupFrames() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.replace(R.id.frame_one, tagsListFragment);
        ft.replace(R.id.frame_two, noteList);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void setupMinorFrames(Fragment frag2) {
        noteList = new NoteListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        ft.replace(R.id.frame_one, noteList);
        ft.replace(R.id.frame_two, frag2);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    //Set the title as the user's name
    private void setNameTitle() {

        databaseHelper = new DatabaseHelper(this, Constants.USER_DATA_TABLE, null, 1);

        username = databaseHelper.getUsername();
        String temp = username + "'s Notes";
        setTitle(temp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_editor_menu, menu);

        menu.getItem(0).setVisible(save);
        menu.getItem(1).setVisible(spell);
        menu.getItem(2).setVisible(analyze);
        menu.getItem(3).setVisible(share);
        menu.getItem(4).setVisible(delete);
        menu.getItem(5).setVisible(searchButton);
        menu.getItem(6).setVisible(lists);
        menu.getItem(7).setVisible(settings);

        return super.onCreateOptionsMenu(menu);
    }

    //Called when an icon in the action bar is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.settings:
                /*
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                break;
                */
                if (ScreenSize.isLargeScreen(this)) {
                    settingsFragment = new SettingsFragment();
                    setupMinorFrames(settingsFragment);
                } else {
                    Intent intent1 = new Intent(this, SettingsActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.tagListIcon:
                showTags();
                break;
            case R.id.save:
                noteEditorFragment.saveNote();
                noteList.updateCursor();
                showTags();
                break;
            case R.id.share:
                noteEditorFragment.shareNote();
                break;
            case R.id.delete:
                deleteNote();
                break;
            case R.id.analyze:
                noteEditorFragment.analyzeNotes();
                break;
            case R.id.spell:
                noteEditorFragment.checkSpelling();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNoteListRight() {
        noteList = new NoteListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_two, noteList);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showTags() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_one, tagsListFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

        showNoteListRight();

        setAllFalse();
        searchButton = true;
        settings = true;
        invalidateOptionsMenu();
    }

    private void showNotesListOnLeft() {
        noteList = new NoteListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_one, noteList);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void startEditor() {
        noteEditorFragment = new NoteEditorFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_two, noteEditorFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

        setAllTrue();
        searchButton = false;
        invalidateOptionsMenu();
    }

    @Override
    public void onNoteListItemClicked(int noteId, String noteTitle, String noteBody) {
        startEditor();
        showNotesListOnLeft();

        id = noteId;
        title = noteTitle;
        body = noteBody;
    }

    private void setAllFalse() {
        edit = false;
        share = false;
        delete = false;
        searchButton = false;
        lists = false;
        settings = false;
        save = false;
        spell = false;
        analyze = false;
    }

    private void setAllTrue() {
        edit = true;
        share = true;
        delete = true;
        searchButton = true;
        lists = true;
        settings = true;
        save = true;
        spell = true;
        analyze = true;
    }

    @Override
    public void clickTagItem(String tag) {
        noteList.setTag(tag);
    }

    @Override
    public void loadedNoteEditorViews() {
        if (title != null && body != null && id != -1) {
            noteEditorFragment.setTextViews(title, body, id);
        }
    }

    private void clearNoteData() {
        title = null;
        body = null;
        id = -1;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() != 0) {
            showTags();
            clearNoteData();
        } else {
            super.onBackPressed();
        }
    }

    //Delete the current note
    private void deleteNote() {
        if (id != -1) {
            databaseHelper.deleteNote(id);
            showTags();
        } else {
            showTags();
        }
    }

    @Override
    public void onSettingsItemClicked(int position) {
        switch (position) {
            case 0:
                //Theme code will go here
                break;
            case 1:
                settingsFragment.deleteName();
                break;
            case 2:
                //startAboutActivity();
                aboutFragment = new AboutFragment();
                settingsFragment.setupMinorFrames(aboutFragment);

                break;
            case 3:
                settingsFragment.deleteAllData(settingsFragment.getView());
                break;
            case 4:
                settingsFragment.exportData();
                break;

        }
    }

}
