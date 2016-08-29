/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This Activity queries all of the tags from the database and displays them in a
 * ListView. When one of the items is clicked, they user if sent to a NotesListActivity displaying
 * all of the notes with the tag selected.
 */

package com.ser210.cyr.clevernotes2.Fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.ser210.cyr.clevernotes2.CursorAdapters.AlternatingColorCursorAdapter;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes_sdk21.R;

public class TagsListFragment extends Fragment implements AdapterView.OnItemClickListener {

    Cursor cursor;
    CursorAdapter cursorAdapter;
    DatabaseHelper databaseHelper;
    ListView listView;
    String selectQuery;

    TagItemClickedListener tagItemClickedListener;

    //This method is called when the Activity is first created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tags_list, container, false);
    }

    //Initialize all of the global variables and set the title of the Activity
    @Override
    public void onViewCreated(View view, Bundle bundle) {

        tagItemClickedListener = (TagItemClickedListener) getActivity();
        listView = (ListView) getActivity().findViewById(R.id.tags_list);

        initDatabaseVariables();
        initListView();
    }

    //Initialize the ListView with the Adapter and OnItemClickListener
    private void initListView() {
        //Create OnLongClickListener that deletes the Tag when held
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                cursor.moveToPosition(pos);
                databaseHelper.deleteTag(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));
                updateCursor();
                return true;
            }
        });

        //Display the query results in the list view
        if (cursor.getCount() > 0) {
            cursorAdapter = new AlternatingColorCursorAdapter(getActivity(), R.layout.simple_dark_note_item, cursor, new String[]{Constants
                    .KEY_TAG}, new int[]{R.id.simple_white_list_item_text1}, 0);
            if (listView != null) {
                listView.setAdapter(cursorAdapter);
            }
            if (listView != null) {
                listView.setOnItemClickListener(this);
            }
        }
    }

    //Initialize the Cursor and the Database Helper
    private void initDatabaseVariables() {
        databaseHelper = new DatabaseHelper(getActivity(), Constants.NOTES_TABLE, null, 1);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        selectQuery = "SELECT * FROM " + Constants.TAGS_TABLE + " GROUP BY " + Constants.KEY_TAG + " ORDER BY " + Constants.KEY_TAG + " ASC";
        cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
    }

    //Called when an item in the ListView is clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cursor.moveToPosition(position);
        String tag = cursor.getString(1);
        tagItemClickedListener.clickTagItem(tag);
    }

    //Update the cursor and swap it for the old one with the CursorAdapter
    private void updateCursor() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        //Requery the database to get updated information
        cursor = database.rawQuery(selectQuery, null);
        cursorAdapter.changeCursor(cursor);
    }

    //Interface that alerts the main activity when a tag is clicked
    public interface TagItemClickedListener{
        public void clickTagItem(String tag);
    }
}
