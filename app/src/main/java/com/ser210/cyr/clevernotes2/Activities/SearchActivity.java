/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This Activity allows the user to search through all of their notes by a word or phrase
 * in the title, body, or tags of the note. This is done by using an onTextChanged listener in the EditText
 * and querying the database with the search term.
 */

package com.ser210.cyr.clevernotes2.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.ser210.cyr.clevernotes2.CursorAdapters.AlternatingColorCursorAdapter;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes2.HelperClasses.ScreenSize;
import com.ser210.cyr.clevernotes_sdk21.R;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    EditText searchBox;
    ListView listView;

    Cursor cursor;
    CursorAdapter cursorAdapter;
    DatabaseHelper databaseHelper;

    boolean isLarge;
    boolean typed;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Search");
        init();
    }

    private void init() {

        typed = false;
        isLarge = ScreenSize.isLargeScreen(this);

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        if (myFab != null) {
            myFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (!isLarge) {
                        Intent intent = new Intent(SearchActivity.this, NoteEditorActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                        intent.putExtra("newNote", true);
                        startActivity(intent);
                    }
                }
            });
        }

        initDatabaseVariables();

        initListViewAndSearchBox();
    }

    private void initListViewAndSearchBox() {
        listView = (ListView) findViewById(R.id.search_results_list);
        searchBox = (EditText) findViewById(R.id.search_box);

        if (listView != null) {
            listView.setOnItemClickListener(this);
        }

        if (listView != null) {
            listView.setAdapter(cursorAdapter);
        }

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            //When the text is changed, this method is called to update the cursor
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                typed = true;
                updateCursor();
            }
        });
    }

    private void initDatabaseVariables() {
        databaseHelper = new DatabaseHelper(this, Constants.NOTES_TABLE, null, 1);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE, null);

        cursorAdapter = new AlternatingColorCursorAdapter(this, R.layout.white_note_list_item, cursor,
                new String[]{Constants.KEY_TITLE, Constants.KEY_BODY, Constants.KEY_DATE_FORMAT},
                new int[]{R.id.list_textView1, R.id.list_textView2, R.id.date_textView},0);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        updateCursor();
    }

    //Update the cursor based on the search term entered into the EditText field
    private void updateCursor() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String term = searchBox.getText().toString();

        if(term.length() > 0) {
            cursor = databaseHelper.searchByNoteOrTag(term);
        }
        else{
            cursor = databaseHelper.getAllNotesCursor();
        }

        cursorAdapter.changeCursor(cursor);
    }

    //This is called when an item in the ListView is clicked. It sends the user to the NoteViewer Class.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursor.moveToPosition(position);

        ScreenSize.isLargeScreen(this);

        if (!isLarge) {
            startNoteEditorForSmallScreens();

        } else {
            startNoteEditorForLargeScreens();
        }
    }

    private void startNoteEditorForLargeScreens() {
        if (typed) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(Constants.NOTE_ID)));
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
            startActivity(intent);
        }
    }

    private void startNoteEditorForSmallScreens() {
        if (typed) {
            Intent intent = new Intent(this, NoteEditorActivity.class);
            intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(Constants.NOTE_ID)));
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, NoteEditorActivity.class);
            intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
            startActivity(intent);
        }
    }
}
