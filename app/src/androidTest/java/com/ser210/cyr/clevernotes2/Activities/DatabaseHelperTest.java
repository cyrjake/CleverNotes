/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This class tests the Database helper class insertions, deletions and updates to the
 * database.
 */

package com.ser210.cyr.clevernotes2.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import com.ser210.cyr.clevernotes2.EntityClasses.Note;
import com.ser210.cyr.clevernotes2.EntityClasses.Tag;
import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes2.HelperClasses.StringFormatting;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    DatabaseHelper databaseHelper;
    SQLiteDatabase database;

    List<Long> list;

    //Initialize the database helper variable before starting testing
    @Before
    public void setUp() throws Exception {

        list = new ArrayList<>();
        list.add(0l);

        databaseHelper = new DatabaseHelper(getTargetContext(), "notes_table", null, 1);
    }

    //Test the insert note method
    @Test
    public void testInsertNote() throws Exception {

        database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE, null);
        int originalCount = cursor.getCount();

        databaseHelper.insertNote("title", "body", list);

        cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE, null);
        int newCount = cursor.getCount();
        cursor.close();

        assertEquals(originalCount + 1, newCount);

    }

    //Test the update note method
    @Test
    public void testUpdateNote() throws Exception {
        databaseHelper.updateNote(0, "new", "body", "tags");
        database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM notes_table", null);
        cursor.moveToFirst();

        String text = cursor.getString(cursor.getColumnIndex("title"));

        cursor.close();
        assertEquals("new", text);
    }

    //Test the delete note method
    @Test
    public void testDeleteNote() throws Exception {
        long id = databaseHelper.insertNote("1234", "body", list);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        databaseHelper.deleteNote((int) id);

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE
                + " WHERE  " + Constants.KEY_TITLE + " LIKE '1234'", null);

        assertEquals(0, cursor.getCount());
    }

    //Test the insert tag method
    @Test
    public void testInsertTag() throws Exception {
        long id = databaseHelper.insertTag("1234");
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.TAGS_TABLE
                + " WHERE  " + Constants.KEY_TAG + " LIKE '1234'", null);

        assertEquals(1, cursor.getCount());
    }

    //Test the delete tag method
    @Test
    public void testDeleteTag() throws Exception {
        long id = databaseHelper.insertTag("1234");
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        databaseHelper.deleteTag((int) id);

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.TAGS_TABLE
                + " WHERE  " + Constants.KEY_TAG + " LIKE '1234'", null);

        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    //Test the contains tag method
    @Test
    public void testContainsTag() throws Exception {
        long id = databaseHelper.insertTag("5678");
        assertEquals(true, databaseHelper.containsTag("5678"));
    }

    //Test the get note method
    @Test
    public void getNoteTest() throws Exception {
        long id = databaseHelper.insertNote("title", "body", list);
        Note note = databaseHelper.getNote((int) id);

        assertEquals(id, note.getId());
        assertEquals("title", note.getTitle());
        assertEquals("body", note.getBody());
        assertEquals(StringFormatting.getFormattedDate(), note.getFormattedDate());
    }

    //Test the get tag method
    @Test
    public void getTagTest() throws Exception {
        long id = databaseHelper.insertTag("title");
        Tag tag = databaseHelper.getTag((int) id);

        assertEquals(id, tag.getId());
        assertEquals("Title", tag.getTag());
    }
}