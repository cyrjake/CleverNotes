/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This class contains constants for use in the DatabaseHelper class and classes that
 * access the database. It also contains other constants used throughout the app.
 */

package com.ser210.cyr.clevernotes2.HelperClasses;

public class Constants {

    //Database Constants

    //Tables
    public static final String NOTES_TABLE = "notes_table";
    public static final String TAGS_TABLE = "tags_table";
    public static final String NOTE_TAGS_TABLE = "notes_tags_table";
    public static final String USER_DATA_TABLE = "user_data_table";

    //Table Columns
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String TAG_CREATED = "created";
    public static final String KEY_MODIFIED = "modified";
    public static final String KEY_DATE_FORMAT = "date_format";
    public static final String KEY_TAG = "tag";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_BACKGROUND = "background_color";

    //Combined Table
    public static final String TAG_ID = "tag_id";
    public static final String NOTE_ID = "note_id";

    //Other Constants
    public static final String LOG_TAG = "Clever Notes";
}
