/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This is an ASyncTask class that gets keyword sentiment suggestions for referenced text and
 *  sets the tags section of the note editor to the found keywords.
 */

package com.ser210.cyr.clevernotes2.ASyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.ser210.cyr.clevernotes2.HelperClasses.Sentiment;
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

public class FetchSentiment extends AsyncTask<String, Void, String> {

    HttpURLConnection urlConnection;
    BufferedReader reader;
    InputStream inputStream;

    private NoteEditorFragment noteEditorFragment = null;
    private NoteEditorActivity noteEditorActivity = null;


    public FetchSentiment(NoteEditorFragment parentActivity) {
        this.noteEditorFragment = parentActivity;
    }

    public FetchSentiment(NoteEditorActivity parentActivity) {
        this.noteEditorActivity = parentActivity;
    }


    //This method is called automatically when the ASyncTask.execute method is called.
    //This method accesses the data from the API and stores it in global variables.
    @Override
    protected String doInBackground(String... params) {

        String apiKey = "9zL1qeZ422msheTDl7URdXQixxlFp1UdYegjsnEfdk4NDF3WsB";
        String sentiment = "";

        try {
            //Create a new connection to the API URL with the added location information and API key
            URL url = new URL("https://twinword-sentiment-analysis.p.mashape.com/analyze/?text=" + params[0] + "&mashape-key=" + apiKey);
            urlConnection = (HttpURLConnection) url.openConnection();

            //Create a new InputStream surrounded by a BufferedReader to read in the JSON data.
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            //Retrieve the JSON string from the Buffered Reader
            JSONObject jsonObject = new JSONObject(SpellCheck.getStringFromBuffer(reader));

            //Receive the sentiment and the rating of the sentiment from the JSONObject
            sentiment = Sentiment.getSentimentType(jsonObject);

            //rating = Sentiment.getSentimentRating(jsonObject);

        } catch (IOException | JSONException e1) {
            Log.e("Clever Notes", e1.getMessage());
        }
        return sentiment;
    }

    //This method is called after the doInBackground method and updates the layout
    protected void onPostExecute(String result) {
        result = result.trim();

        if (result.length() > 0) {
            if(noteEditorActivity != null){
                noteEditorActivity.addTagsToList(result);
            }
            else if(noteEditorFragment != null){
                noteEditorFragment.addTagsToList(result);
            }
        }
    }
}

