/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This Activity displays a list of notes the notes depending on where the user came from.
 */

package com.ser210.cyr.clevernotes2.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.ser210.cyr.clevernotes2.CursorAdapters.AlternatingColorCursorAdapter;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes_sdk21.R;

public class NoteListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Cursor cursor;
    CursorAdapter adapter;
    DatabaseHelper databaseHelper;

    String tag, name;
    String selectQuery;

    ListView listView;

    //This method is called when the Activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        setNameTitle();
        init();
    }

    private void setNameTitle() {
        databaseHelper = new DatabaseHelper(this, Constants.USER_DATA_TABLE, null, 1);
        name = databaseHelper.getUsername();
        String title = name + "'s Notes";
        setTitle(title);
    }

    //Initializes all of the global variables and populates the ListView with Notes
    private void init() {

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        if (myFab != null) {
            myFab.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent1 = new Intent(NoteListActivity.this, NoteEditorActivity.class);
                    startActivity(intent1);
                }
            });

            initDatabaseVariables();
            initListView();
        }
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.notes_list_view);

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

        adapter = new AlternatingColorCursorAdapter(this, R.layout.white_note_list_item, cursor,
                new String[]{Constants.KEY_TITLE, Constants.KEY_BODY, Constants.KEY_DATE_FORMAT},
                new int[]{R.id.list_textView1, R.id.list_textView2, R.id.date_textView}, 0);

        if (listView != null) {
            listView.setAdapter(adapter);
        }
        if (listView != null) {
            listView.setOnItemClickListener(this);
        }
    }

    private void initDatabaseVariables() {
        databaseHelper = new DatabaseHelper(this, Constants.NOTES_TABLE, null, 1);
        selectQuery = "SELECT * FROM " + Constants.NOTES_TABLE + " ORDER BY "
                + Constants.KEY_MODIFIED + " DESC";

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        cursor = database.rawQuery(selectQuery, null);
    }

    //Called when the app restarts after being stopped and updates the cursor.
    @Override
    public void onRestart() {
        super.onRestart();
        updateCursor();
    }

    //Updates the cursor by querying for new data
    private void updateCursor() {

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        if (tag != null) {
            cursor = databaseHelper.getAllNotesByTag(tag);
        } else {
            cursor = database.rawQuery(selectQuery, null);
        }
        adapter.changeCursor(cursor);
    }

    //Called when the Activity is destroyed and closes the DatabaseHelper
    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursor.moveToPosition(position);

        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("title", cursor.getString(cursor.getColumnIndex(Constants.KEY_TITLE)));
        intent.putExtra("body", cursor.getString(cursor.getColumnIndex(Constants.KEY_BODY)));
        intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
        intent.putExtra("new", false);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.tagListIcon:
                Intent intent2 = new Intent(this, TagsListActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
