/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This Activity allow users to view their note before choosing to either edit it or return
 * to the previous screen. It also allows the user to delete the note.
 */

package com.ser210.cyr.clevernotes2.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ser210.cyr.clevernotes2.ASyncTasks.FetchKeywords;
import com.ser210.cyr.clevernotes2.ASyncTasks.FetchSentiment;
import com.ser210.cyr.clevernotes2.ASyncTasks.FetchSpellingSuggestions;
import com.ser210.cyr.clevernotes2.EntityClasses.Note;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes2.HelperClasses.StringFormatting;
import com.ser210.cyr.clevernotes_sdk21.R;

import java.util.ArrayList;
import java.util.List;

public class NoteEditorActivity extends AppCompatActivity {

    private EditText title, body, tags;
    private int noteId;
    private DatabaseHelper databaseHelper;

    //This method is called when the Activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        init();
        dealWithIntent();
    }

    //Initialize all variables
    private void init() {

        databaseHelper = new DatabaseHelper(this, Constants.NOTES_TABLE, null, 1);

        title = (EditText) findViewById(R.id.title_edit_text);
        body = (EditText) findViewById(R.id.body);
        tags = (EditText) findViewById(R.id.tags_input);

        title.getBackground().setColorFilter(Color.parseColor("#50ffffff"), PorterDuff.Mode.SRC_IN);
        body.getBackground().setColorFilter(Color.parseColor("#50ffffff"), PorterDuff.Mode.SRC_IN);
        tags.getBackground().setColorFilter(Color.parseColor("#50ffffff"), PorterDuff.Mode.SRC_IN);

        setTitle("New Note");
        noteId = -1;
    }

    //Get all data from the intent, if any
    private void dealWithIntent() {
        Intent intent = getIntent();

        if (intent != null) {
            noteId = intent.getIntExtra("id", -1);

            if (noteId != -1) {
                setTitle("Update Note");

                Note note = databaseHelper.getNote(noteId);
                if (note != null) {
                    title.setText(note.getTitle());
                    body.setText(note.getBody());
                    tags.setText(note.getTags());
                }
            }
        }
    }

    //Inflate the Menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_editor_menu, menu);

        menu.getItem(5).setVisible(false);
        menu.getItem(6).setVisible(false);
        menu.getItem(7).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    //Called when an item from the action bar is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveNote();
                break;
            case R.id.share:
                shareNote();
                break;
            case R.id.delete:
                deleteNote();
                break;
            case R.id.analyze:
                analyzeNotes();
                break;
            case R.id.spell:
                checkSpelling();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Check the spelling of the body text
    private void checkSpelling() {
        String text = body.getText().toString().trim();

        if (text.length() > 0) {
            FetchSpellingSuggestions fetchSpellingSuggestions = new FetchSpellingSuggestions(this);
            fetchSpellingSuggestions.execute(StringFormatting.prepareStringForAPI(text));
        } else {
            displayToast("Please create your note first");
        }
    }

    //Analyze the note for sentiment and important keywords
    private void analyzeNotes() {
        String bodyOfNote = body.getText().toString().trim();

        if (bodyOfNote.length() > 0) {

            bodyOfNote = StringFormatting.prepareStringForAPI(body.getText().toString());

            FetchKeywords fetchKeywords = new FetchKeywords(this);
            fetchKeywords.execute(bodyOfNote);

            FetchSentiment fetchSentimentAnalysis = new FetchSentiment(this);
            fetchSentimentAnalysis.execute(bodyOfNote);

        } else {
            displayToast("Please finish your note first");
        }
    }

    //Delete the current note
    private void deleteNote() {
        if (noteId != -1) {
            databaseHelper.deleteNote(noteId);
            sendToMainActivity();
        } else {
            sendToMainActivity();
        }
    }

    //Save the current note to the database
    private void saveNote() {
        if (noteEntered()) {

            String titleText = title.getText().toString();
            String bodyText = body.getText().toString();
            String tagText = tags.getText().toString();

            DatabaseHelper databaseHelper = new DatabaseHelper(this, Constants.NOTES_TABLE, null, 1);

            if (isNewNote()) {
                List<Long> tag_ids = insertAllTags(tagText, databaseHelper);
                databaseHelper.insertNote(titleText, bodyText, tag_ids);
                displayToast("Note Saved");
            } else {
                databaseHelper.updateNote(noteId, titleText, bodyText, tagText);
                displayToast("Note Updated");
            }
            sendToMainActivity();
        } else {
            displayToast("Please finish creating your note");
        }
    }

    @NonNull
    private List<Long> insertAllTags(String tagText, DatabaseHelper databaseHelper) {
        String[] allTags = tagText.split(",");

        List<Long> tag_ids = new ArrayList<Long>();

        for (String s : allTags) {
            long id = databaseHelper.insertTag(s);
            tag_ids.add(id);
        }
        return tag_ids;
    }

    //Start the Main Activity
    private void sendToMainActivity() {
        Intent intent = new Intent(this, NoteListActivity.class);
        startActivity(intent);
    }

    //Checks if the current note is a new note or one being updated
    private boolean isNewNote() {
        if (noteId == -1) {
            return true;
        } else {
            return false;
        }
    }

    //Display a Toast with the referenced String
    private void displayToast(String message) {
        Toast.makeText(NoteEditorActivity.this, message, Toast.LENGTH_LONG).show();
    }

    //Checks if text is entered in all of the fields of the note
    private boolean noteEntered() {
        if (body.getText().toString().length() > 0 && tags.getText().toString().length() > 0
                && title.getText().toString().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Share the note using an implicit intent. Includes the title and body of the note
    private void shareNote() {
        if (noteEntered()) {
            Intent intent = new Intent();
            intent.setType("text/plain");
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, title.getText().toString() + "\n\n" + body.getText().toString());

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            displayToast("Please finish your note first");
        }
    }

    //Add new tags generated to the tags list
    public void addTagsToList(String newTags) {
        String currentTags = tags.getText().toString();

        if (currentTags.trim().length() > 0) {
            tags.setText(currentTags + ", " + newTags);
        } else {
            tags.setText(newTags);
        }
    }

    //Set the body of the note. Used by the FetchSpellingSuggestions class to update the text
    public void setBody(String s) {
        body.setText(s);
    }

    //Returns the text in the body edittext view. Used by the FetchSpellingSuggestions class
    public String getBody() {
        return body.getText().toString();
    }

    public void moveCursorToEnd() {
        if (body != null) {
            body.setSelection(body.getText().length());
        }
    }
}

