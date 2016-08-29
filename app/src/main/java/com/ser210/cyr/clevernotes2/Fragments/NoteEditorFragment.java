/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This Activity allow users to view their note before choosing to either edit it or return
 * to the previous screen. It also allows the user to delete the note.
 */

package com.ser210.cyr.clevernotes2.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ser210.cyr.clevernotes2.ASyncTasks.FetchKeywords;
import com.ser210.cyr.clevernotes2.ASyncTasks.FetchSentiment;
import com.ser210.cyr.clevernotes2.ASyncTasks.FetchSpellingSuggestions;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes2.HelperClasses.StringFormatting;
import com.ser210.cyr.clevernotes_sdk21.R;

import java.util.ArrayList;
import java.util.List;

public class NoteEditorFragment extends Fragment {

    private EditText title, body, tags;
    private TextView titleView;
    private int noteId;
    private DatabaseHelper databaseHelper;

    boolean newNote;

    ViewsLoadedListener viewsLoadedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_note_editor, container, false);
    }

    //Called after the view is finished being setup
    @Override
    public void onViewCreated(View view, Bundle bundle) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        databaseHelper = new DatabaseHelper(getActivity(), Constants.NOTES_TABLE, null, 1);

        viewsLoadedListener = (ViewsLoadedListener) getActivity();

        titleView = (TextView) getActivity().findViewById(R.id.note_editor_title);
        title = (EditText) getActivity().findViewById(R.id.title_edit_text);
        body = (EditText) getActivity().findViewById(R.id.body);
        tags = (EditText) getActivity().findViewById(R.id.tags_input);

        noteId = -1;

        viewsLoadedListener.loadedNoteEditorViews();
    }

    //Check the spelling of the body text
    public void checkSpelling() {
        String text = body.getText().toString().trim();

        if (text.length() > 0) {
            FetchSpellingSuggestions fetchSpellingSuggestions = new FetchSpellingSuggestions(this);
            fetchSpellingSuggestions.execute(StringFormatting.prepareStringForAPI(text));
        } else {
            displayToast("Please create your note first");
        }
    }

    //Analyze the note for sentiment and important keywords
    public void analyzeNotes() {
        String bodyOfNote = body.getText().toString().trim();

        if (bodyOfNote.length() > 0) {
            bodyOfNote = StringFormatting.prepareStringForAPI(body.getText().toString());
            startASyncTasks(bodyOfNote);
        } else {
            displayToast("Please finish your note first");
        }
    }

    private void startASyncTasks(String bodyOfNote) {
        FetchKeywords fetchKeywords = new FetchKeywords(this);
        fetchKeywords.execute(bodyOfNote);

        FetchSentiment fetchSentimentAnalysis = new FetchSentiment(this);
        fetchSentimentAnalysis.execute(bodyOfNote);
    }

    //Save the current note to the database
    public void saveNote() {
        if (noteEntered()) {

            String titleText = title.getText().toString();
            String bodyText = body.getText().toString();
            String tagText = tags.getText().toString();

            if (isNewNote()) {
                List<Long> tag_ids = insertAllTags(tagText);
                databaseHelper.insertNote(titleText, bodyText, tag_ids);

            } else {
                databaseHelper.updateNote(noteId, titleText, bodyText, tagText);
            }
        } else {
            displayToast("Please finish creating your note");
        }
    }

    @NonNull
    private List<Long> insertAllTags(String tagText) {
        String[] allTags = tagText.split(",");

        List<Long> tag_ids = new ArrayList<Long>();

        for (String s : allTags) {
            long id = databaseHelper.insertTag(s);
            tag_ids.add(id);
        }
        return tag_ids;
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
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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

    public void setTextViews(String newTitle, String newBody, int id) {

        title.setText(newTitle);
        body.setText(newBody);

        String tag = databaseHelper.getAllTagsByNote(id);
        tags.setText(tag);

        noteId = id;

        if (isNewNote()) {
            titleView.setText("New Note");
        } else {
            titleView.setText("Update Note");
        }
    }

    public interface ViewsLoadedListener {
        public void loadedNoteEditorViews();
    }

    //Share the note using an implicit intent. Includes the title and body of the note
    public void shareNote() {
        if (title.getText() != null && body.getText() != null) {
            Intent intent = new Intent();
            intent.setType("text/plain");
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, title.getText().toString() + "\n\n" + body.getText().toString());
            startActivity(intent);
        } else {
            displayToast("Please create a note first");
        }
    }

    public void moveCursorToEnd() {
        if (body != null) {
            body.setSelection(body.getText().length());
        }
    }

}

