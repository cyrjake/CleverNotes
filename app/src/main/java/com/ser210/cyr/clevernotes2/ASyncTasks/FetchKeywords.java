/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This is an ASyncTask class that gets keyword suggestions for referenced text and
 *  sets the tags section of the note editor to the found keywords.
 */

package com.ser210.cyr.clevernotes2.ASyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.KeywordExtraction;
import com.ser210.cyr.clevernotes2.HelperClasses.SpellCheck;
import com.ser210.cyr.clevernotes2.Fragments.NoteEditorFragment;
import com.ser210.cyr.clevernotes2.Activities.NoteEditorActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//keywords in the note for tagging purposes
public class FetchKeywords extends AsyncTask<String, Void, String[]> {

    HttpURLConnection urlConnection;
    BufferedReader reader;
    InputStream inputStream;
    String[] keywords;

    private NoteEditorFragment noteEditorFragment = null;
    private NoteEditorActivity noteEditorActivity = null;


    public FetchKeywords(NoteEditorFragment parentActivity) {
        this.noteEditorFragment = parentActivity;
    }

    public FetchKeywords(NoteEditorActivity parentActivity) {
        this.noteEditorActivity = parentActivity;
    }

    //This method is called automatically when the ASyncTask.execute method is called.
    //This method accesses the data from the API and stores it in global variables.
    @Override
    protected String[] doInBackground(String... params) {

        String apiKey = "9zL1qeZ422msheTDl7URdXQixxlFp1UdYegjsnEfdk4NDF3WsB";

        try {
            //Create a new connection to the API URL with the added location information and API key
            URL url = new URL("https://alchemy.p.mashape.com/text/TextGetRankedKeywords?keyword" +
                    "ExtractMode=normal&maxRetrieve=5&outputMode=json&text="
                    + params[0] + "&mashape-key=" + apiKey);

            urlConnection = (HttpURLConnection) url.openConnection();

            //Create a new InputStream surrounded by a BufferedReader to read in the JSON data.
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String jsonString = SpellCheck.getStringFromBuffer(reader);

            if (jsonString != null) {
                //Retrieve the JSON string from the Buffered Reader
                JSONObject jsonObject = new JSONObject(jsonString);

                //Retrieve the keywords from the JSON Object
                keywords = KeywordExtraction.getKeywordsFromJSON(jsonObject);
            }

        } catch (IOException | JSONException e1) {
            Log.e(Constants.LOG_TAG, e1.getMessage());
        }
        return keywords;
    }

    //This method is called after the doInBackground method and updates the layout with the keywords
    protected void onPostExecute(String[] result) {
        if (result != null) {
            String newTags = KeywordExtraction.formatStringForView(result);

            if(noteEditorFragment != null){
                noteEditorFragment.addTagsToList(newTags);
            }
            else if(noteEditorActivity != null){
                noteEditorActivity.addTagsToList(newTags);
            }
        }
    }
}