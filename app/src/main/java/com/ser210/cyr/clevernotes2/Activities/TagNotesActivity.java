/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This Activity queries all of the tags from the database and displays them in a
 * ListView. When one of the items is clicked, they user if sent to a NotesListActivity displaying
 * all of the notes with the tag selected.
 */

package com.ser210.cyr.clevernotes2.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.ser210.cyr.clevernotes2.CursorAdapters.AlternatingColorCursorAdapter;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes_sdk21.R;

public class TagNotesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Cursor cursor;
    CursorAdapter cursorAdapter;
    DatabaseHelper databaseHelper;
    ListView listView;
    String tag;

    //This method is called when the Activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_notes);
        init();
    }

    //Initialize all of the global variables and set the title of the Activity
    private void init() {

        listView = (ListView) findViewById(R.id.tags_notes_list_view);

        databaseHelper = new DatabaseHelper(this, Constants.NOTES_TABLE, null, 1);

        Intent intent = getIntent();
        tag = intent.getStringExtra("tag");

        cursor = databaseHelper.getAllNotesByTag(tag);

        setCountTitle();

        //Create OnLongClickListener that deletes the Tag when held
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                cursor.moveToPosition(pos);
                databaseHelper.deleteNote(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
                updateCursor();
                return true;
            }
        });

        //Display the query results in the list view
        if (cursor.getCount() > 0) {
            cursorAdapter = new AlternatingColorCursorAdapter(this, R.layout.simple_white_note_item, cursor, new String[]{Constants
                    .KEY_TITLE}, new int[]{R.id.simple_white_list_item_text1}, 0);
            if (listView != null) {
                listView.setAdapter(cursorAdapter);
            }
            if (listView != null) {
                listView.setOnItemClickListener(this);
            }
        }
    }

    private void setCountTitle() {
        setTitle("Notes (" + cursor.getCount() + ")");
    }

    //Called when an item in the ListView is clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursor.moveToPosition(position);

        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("id", cursor.getInt(0));

        startActivity(intent);
    }

    //Update the cursor and swap it for the old one with the CursorAdapter
    private void updateCursor() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        //Requery the database to get updated information
        cursor = databaseHelper.getAllNotesByTag(tag);
        cursorAdapter.changeCursor(cursor);

        //Change the activity title
        setCountTitle();
    }

    //Called when the Activity is restarted. Update the cursor
    @Override
    public void onRestart() {
        super.onRestart();
        updateCursor();
    }

    //Called when the activity is destroyed. Closes the DatabaseHelper
    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
        cursor.close();
    }
}
