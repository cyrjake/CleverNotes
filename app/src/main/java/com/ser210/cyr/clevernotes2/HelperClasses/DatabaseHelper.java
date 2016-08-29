/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This class allows for easy access and manipulation of data in the database.
 * It also creates the tables when the app is started for the first time or if they are dropped.
 */

package com.ser210.cyr.clevernotes2.HelperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ser210.cyr.clevernotes2.EntityClasses.Note;
import com.ser210.cyr.clevernotes2.EntityClasses.Tag;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //The constructor that receives context, the table name, cursor factory, and version of the database
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //This method is called when the Activity is first created and creates the tables if they don't exist
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createNote = "CREATE TABLE " + Constants.NOTES_TABLE + " ( " + Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.KEY_TITLE + " TEXT NOT NULL, " + Constants.KEY_BODY + " TEXT NOT NULL, " + Constants.TAG_CREATED + " TEXT NOT NULL," +
                Constants.KEY_MODIFIED + " TEXT NOT NULL, " + Constants.KEY_DATE_FORMAT + " TEXT NOT NULL);";

        String createTags = "CREATE TABLE " + Constants.TAGS_TABLE + " ( " + Constants.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.KEY_TAG + " TEXT NOT NULL, " + Constants.TAG_CREATED + " TEXT NOT NULL);";

        // todo_tag table create statement
        String createCombinedTable = "CREATE TABLE " + Constants.NOTE_TAGS_TABLE + "(" + Constants.KEY_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + Constants.NOTE_ID + " INTEGER, " + Constants.TAG_ID + " INTEGER)";

        String createUserDataTable = "CREATE TABLE " + Constants.USER_DATA_TABLE + "(" + Constants.KEY_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + Constants.KEY_USERNAME + " TEXT NOT NULL);";

        db.execSQL(createNote);
        db.execSQL(createTags);
        db.execSQL(createCombinedTable);
        db.execSQL(createUserDataTable);
    }

    //Insert a note into the database
    public long insertNote(String title, String body, List<Long> tag_ids) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.KEY_TITLE, title);
        contentValues.put(Constants.KEY_BODY, body);
        contentValues.put(Constants.TAG_CREATED, StringFormatting.getDateAndTime());
        contentValues.put(Constants.KEY_MODIFIED, StringFormatting.getDateAndTime());
        contentValues.put(Constants.KEY_DATE_FORMAT, StringFormatting.getFormattedDate());

        long id = database.insert(Constants.NOTES_TABLE, null, contentValues);

        for (Long tag : tag_ids) {
            createNoteTag(id, tag);
        }

        return id;
    }

    public long createNoteTag(long note_id, long tag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long id = 0;
        Tag tag = getTag(tag_id);

        if (tag != null) {
            if (!containsTag(tag.getTag())) {
                ContentValues values = new ContentValues();
                values.put("note_id", note_id);
                values.put("tag_id", tag_id);
                id = db.insert("notes_tags_table", null, values);
            } else {
                ContentValues values = new ContentValues();
                values.put("note_id", note_id);
                values.put("tag_id", tag_id);
                id = db.insert("notes_tags_table", null, values);
            }
        }
        return id;
    }

    //Update a note that is currently in the database
    public void updateNote(int id, String title, String body, String tags) {
        SQLiteDatabase database = this.getWritableDatabase();

        String[] allTags = tags.split(",");
        List<Long> tag_ids = new ArrayList<Long>();

        deleteNoteFromCombined(id);
        deleteNote(id);

        for (String s : allTags) {
            tag_ids.add(insertTag(s));
        }

        insertNote(title, body, tag_ids);

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.KEY_TITLE, title);
        contentValues.put(Constants.KEY_BODY, body);
        contentValues.put(Constants.KEY_MODIFIED, StringFormatting.getDateAndTime());
        contentValues.put(Constants.KEY_DATE_FORMAT, StringFormatting.getFormattedDate());

        Log.v("Clever Notes", "Note Updated");

        database.update(Constants.NOTES_TABLE, contentValues, Constants.KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    //Get a note by id. Returns a note entity
    public Note getNote(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Note note = new Note();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE
                + " WHERE " + Constants.KEY_ID + " = " + id, null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            note.setId(id);
            note.setTitle(cursor.getString(cursor.getColumnIndex(Constants.KEY_TITLE)));
            note.setBody(cursor.getString(cursor.getColumnIndex(Constants.KEY_BODY)));
            note.setCreated(cursor.getString(cursor.getColumnIndex(Constants.TAG_CREATED)));
            note.setModified(cursor.getString(cursor.getColumnIndex(Constants.KEY_MODIFIED)));
            note.setFormattedDate(cursor.getString(cursor.getColumnIndex(Constants.KEY_DATE_FORMAT)));
            note.setTags(getAllTagsByNote(id));
            cursor.close();
            return note;
        }
        cursor.close();
        return null;
    }

    public String getAllNotesToExport() {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE, null);
        cursor.moveToFirst();

        String output = "{";

        if(cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                output = output + "\"title\": \"" + cursor.getString(cursor.getColumnIndex(Constants.KEY_TITLE)) + "\", ";
                output = output + "\"body\": \"" + cursor.getString(cursor.getColumnIndex(Constants.KEY_BODY))  + "\", ";
                output = output + "\"tags\": [" + getAllTagsByNote(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID))) + "],";
                output = output + "\n";
                cursor.moveToNext();
            }
            output = output.trim() + "}";
            return output;
        }
        else{
            return null;
        }
    }

    public Cursor getAllNotesCursor() {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE, null);
    }

    //Delete a note that is stored in the database by the id
    public void deleteNote(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + Constants.NOTES_TABLE + " WHERE " + Constants.KEY_ID + " = " + id);
        Log.v("Clever Notes", "Note Deleted");
        deleteNoteFromCombined(id);
    }

    public void deleteNoteFromCombined(int note_id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + Constants.NOTE_TAGS_TABLE + " WHERE " + Constants.NOTE_ID + " = " + note_id);
        Log.v("Clever Notes", "Note Deleted From Combined Table");
    }

    //Insert a tag into the Tags table of the database
    public long insertTag(String tag) {

        SQLiteDatabase database = this.getWritableDatabase();

        if (!containsTag(tag)) {

            //Capitalize the first letter of the tag
            tag = StringFormatting.capFirstLetter(tag.trim());

            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.KEY_TAG, tag);
            contentValues.put(Constants.TAG_CREATED, StringFormatting.getDateAndTime());

            return database.insert(Constants.TAGS_TABLE, null, contentValues);
        } else {
            return getTagId(tag);
        }
    }

    //Delete a tag that is currently stored in the database by id
    public void deleteTag(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + Constants.TAGS_TABLE + " WHERE " + Constants.KEY_ID + " = " + id);
        deleteTagFromCombined(id);
        Log.v(Constants.LOG_TAG, "Tag Deleted");
    }

    public void deleteTagFromCombined(int tag_id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + Constants.NOTE_TAGS_TABLE + " WHERE " + Constants.TAG_ID + " = " + tag_id);
        Log.v(Constants.LOG_TAG, "Tag Deleted From Combined");
    }

    //Get a tag by id. Returns a tag entity object.
    public Tag getTag(long id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Tag tag = new Tag();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.TAGS_TABLE
                + " WHERE " + Constants.KEY_ID + " = " + id, null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            tag.setId(id);
            tag.setTag(cursor.getString(cursor.getColumnIndex(Constants.KEY_TAG)));
            tag.setCreated(cursor.getString(cursor.getColumnIndex(Constants.TAG_CREATED)));
            cursor.close();
            return tag;
        }
        cursor.close();
        return null;
    }

    //Check if the Tags table contains a referenced tag
    public boolean containsTag(String tag) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.TAGS_TABLE + " WHERE " + Constants.KEY_TAG + " LIKE '" + tag + "'", null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public long getTagId(String tag) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.TAGS_TABLE + " WHERE " + Constants.KEY_TAG + " LIKE '" + tag + "'", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            long id = cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID));
            Log.v("ID", "The id is " + id);
            cursor.close();
            return id;
        } else {
            cursor.close();
            return 0;
        }

    }

    //Returns a cursor with all of the notes that contain a tag for use with  a Cursor Adapter
    public Cursor getAllNotesByTag(String tag_name) {

        String selectQuery = "SELECT  * FROM " + Constants.NOTES_TABLE + " nt, "
                + Constants.TAGS_TABLE + " tg, notes_tags_table ct WHERE tg." + Constants.KEY_ID
                + " = " + "ct.tag_id AND nt." + Constants.KEY_ID + " = "
                + "ct.note_id AND " + Constants.KEY_TAG + " = '" + tag_name + "'";

        Log.e(Constants.LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(selectQuery, null);
    }

    public String getAllTagsByNote(int note_id) {
        String selectQuery = "SELECT * FROM " + Constants.NOTES_TABLE + " nt, "
                + Constants.TAGS_TABLE + " tg, " + Constants.NOTE_TAGS_TABLE + " ct WHERE tg." + Constants.KEY_ID
                + " = " + "ct.tag_id AND nt." + Constants.KEY_ID + " = ct.note_id AND nt." + Constants.KEY_ID + " = " + note_id;

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        List<Tag> tags = new ArrayList<Tag>();

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                Tag tag = new Tag();
                tag.setTag(cursor.getString(cursor.getColumnIndex(Constants.KEY_TAG)));
                tags.add(tag);
                cursor.moveToNext();
            }

            String tagString = StringFormatting.listToCSString(tags);

            if (tagString != null) {
                if (tagString.contains(",")) {
                    StringBuilder sb = new StringBuilder(tagString);
                    sb.deleteCharAt(tagString.lastIndexOf(","));
                    tagString = sb.toString();
                }
            }
            return tagString;

        }
        return null;
    }

    public void deleteAllData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + Constants.NOTES_TABLE);
        database.execSQL("DELETE FROM " + Constants.TAGS_TABLE);
        database.execSQL("DELETE FROM " + Constants.NOTE_TAGS_TABLE);
    }

    public Cursor searchByNoteOrTag(String term) {
        SQLiteDatabase database = getReadableDatabase();

        String searchByTagQuery = "SELECT * FROM " + Constants.NOTES_TABLE + " nt, "
                + Constants.TAGS_TABLE + " tg, " + Constants.NOTE_TAGS_TABLE + " ct WHERE tg." + Constants.KEY_ID
                + " = " + "ct.tag_id AND nt." + Constants.KEY_ID + " = ct.note_id AND tg." +
                Constants.KEY_TAG + " LIKE '%" + term + "%' OR " + Constants.KEY_DATE_FORMAT + " LIKE '%" + term + "%'"
                + " OR " + Constants.KEY_TITLE + " LIKE '%" + term + "%' OR "
                + Constants.KEY_BODY + " LIKE '%" + term + "%' OR " + Constants.KEY_MODIFIED + " LIKE '%" + term + "%'"
                + " GROUP BY " + Constants.KEY_TITLE;

        //Get all notes that match the search term
        return database.rawQuery(searchByTagQuery, null);
    }

    public void insertUsername(String name) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.KEY_USERNAME, name);

        database.insert(Constants.USER_DATA_TABLE, null, contentValues);
    }

    public String getUsername() {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.USER_DATA_TABLE, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            print("There are " + cursor.getCount());
            String name = cursor.getString(cursor.getColumnIndex(Constants.KEY_USERNAME));
            print(name);
            cursor.close();
            return name;
        } else {
            cursor.close();
            return null;
        }
    }

    private void print(String s){
        Log.v(Constants.LOG_TAG, s);
    }

    public void updateUsername(String string){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.KEY_USERNAME, string);
        database.update(Constants.USER_DATA_TABLE, contentValues, Constants.KEY_ID + " = ?", new String[]{String.valueOf(0)});
    }

    public boolean containsUsername(){
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.USER_DATA_TABLE, null);

        if(cursor.getCount() > 0){
            return true;
        }
        else{
            return false;
        }
    }

    //Delete all data in the user data table (Just the username)
    public void deleteUsername(){
        SQLiteDatabase database = getWritableDatabase();
        database.delete(Constants.USER_DATA_TABLE, null, null);
    }

    //Called when the database is updated. Deletes the old tables and recreates them.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + Constants.NOTES_TABLE + " IF EXISTS");
        db.execSQL("DROP TABLE " + Constants.TAGS_TABLE + " IF EXISTS");
        onCreate(db);
    }
}
