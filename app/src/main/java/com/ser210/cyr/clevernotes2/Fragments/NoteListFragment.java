/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This Activity displays a list of notes the notes depending on where the user came from.
 */

package com.ser210.cyr.clevernotes2.Fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.ser210.cyr.clevernotes2.CursorAdapters.AlternatingColorCursorAdapterLight;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes_sdk21.R;

import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.OnItemLongClickListener;

public class NoteListFragment extends Fragment implements OnItemClickListener {

    Cursor cursor;
    CursorAdapter adapter;
    DatabaseHelper databaseHelper;

    NoteClickListener noteClickListener;
    String tag;

    Button showAll;
    ListView listView;

    //This method is called when the Activity is first created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_note_list, container, false);
    }

    //Initializes all of the global variables and populates the ListView with Notes
    @Override
    public void onViewCreated(View view, Bundle bundle) {

        initDatabaseVariables();
        initListView();

        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag = null;
                updateCursor();
            }
        });
    }

    private void initListView() {
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                cursor.moveToPosition(pos);
                databaseHelper.deleteNote(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
                updateCursor();
                return true;
            }
        });

        if (listView != null) {
            Log.v(Constants.LOG_TAG, "Adapter Set");
            listView.setAdapter(adapter);
        }
        if (listView != null) {
            listView.setOnItemClickListener(this);
        }
    }

    private void initDatabaseVariables() {
        noteClickListener = (NoteClickListener) getActivity();
        showAll = (Button) getActivity().findViewById(R.id.show_all_button);
        listView = (ListView) getActivity().findViewById(R.id.notes_list_view);

        databaseHelper = new DatabaseHelper(getActivity(), Constants.NOTES_TABLE, null, 1);

        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE, null);

        adapter = new AlternatingColorCursorAdapterLight(getActivity(), R.layout.white_note_list_item, cursor,
                new String[]{Constants.KEY_TITLE, Constants.KEY_BODY, Constants.KEY_DATE_FORMAT},
                new int[]{R.id.list_textView1, R.id.list_textView2, R.id.date_textView}, 0);
    }

    //Called when an item in the ListView is clicked. Sends the user to the Note Viewer Activity
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursor.moveToPosition(position);

        String title;
        String body;
        int noteID;

        if (tag == null) {
            title = cursor.getString(cursor.getColumnIndex(Constants.KEY_TITLE));
            body = cursor.getString(cursor.getColumnIndex(Constants.KEY_BODY));
            noteID = cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID));
        } else {
            title = cursor.getString(cursor.getColumnIndex(Constants.KEY_TITLE));
            body = cursor.getString(cursor.getColumnIndex(Constants.KEY_BODY));
            noteID = cursor.getInt(cursor.getColumnIndex(Constants.NOTE_ID));
        }

        noteClickListener.onNoteListItemClicked(noteID, title, body);
        updateCursor();
    }

    //Update the cursor based on the search term entered into the EditText field
    public void updateCursor() {
        if (tag == null) {
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE + " ORDER BY " + Constants.KEY_MODIFIED + " ASC", null);
            adapter.changeCursor(cursor);
        } else {
            cursor = databaseHelper.getAllNotesByTag(tag);
            adapter.changeCursor(cursor);
        }
    }

    public interface NoteClickListener {
        public void onNoteListItemClicked(int id, String title, String body);
    }

    public void setTag(String text) {
        tag = text;
        updateCursor();
    }

}
