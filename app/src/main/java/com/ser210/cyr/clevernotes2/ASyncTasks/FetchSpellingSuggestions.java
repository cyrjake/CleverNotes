/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This is an ASyncTask class that gets spelling suggestions for referenced text and
 *  sets the body of the note editor to the fixed text.
 */

package com.ser210.cyr.clevernotes2.ASyncTasks;

import android.os.AsyncTask;

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

public class FetchSpellingSuggestions extends AsyncTask<String, Void, JSONObject> {

    HttpURLConnection urlConnection;
    BufferedReader reader;
    InputStream inputStream;
    String jsonString;

    private NoteEditorFragment noteEditorFragment = null;
    private NoteEditorActivity noteEditorActivity = null;


    public FetchSpellingSuggestions(NoteEditorFragment parentActivity) {
        this.noteEditorFragment = parentActivity;
    }

    public FetchSpellingSuggestions(NoteEditorActivity parentActivity) {
        this.noteEditorActivity = parentActivity;
    }

    //This method is called automatically when the ASyncTask.execute method is called.
    //This method accesses the data from the API and stores it in global variables.
    @Override
    protected JSONObject doInBackground(String... params) {

        String apiKey = "9zL1qeZ422msheTDl7URdXQixxlFp1UdYegjsnEfdk4NDF3WsB";
        JSONObject correctionObject = new JSONObject();

        try {
            //Create a new connection to the API URL with the added location information and API key
            URL url = new URL("https://montanaflynn-spellcheck.p.mashape.com/check/?text=" + params[0] + "&mashape-key=" + apiKey);
            urlConnection = (HttpURLConnection) url.openConnection();

            //Create a new InputStream surrounded by a BufferedReader to read in the JSON data.
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream));

            //Retrieve the JSON string from the Buffered Reader
            jsonString = SpellCheck.getStringFromBuffer(reader);

            //Creates a JSON Object from the JSON text received
            JSONObject jsonObject = new JSONObject(jsonString);

            //Gets a JSON Object with the title corrections
            correctionObject = jsonObject.getJSONObject("corrections");

        } catch (IOException | JSONException e1) {
            e1.printStackTrace();
        }
        return correctionObject;
    }

    //This method is called after the doInBackground an updates the EditText field with the
    //corrected words
    protected void onPostExecute(JSONObject result) {

        String body;

        if(noteEditorActivity != null) {
            body = noteEditorActivity.getBody();
        }
        else{
            body = noteEditorFragment.getBody();
        }

        //Updates each word individually
        if (result != null) {
            for (int i = 0; i < result.length(); i++) {
                String misspelledWord = SpellCheck.getMisspelledWord(result, i);
                String replacement = SpellCheck.getMisspelledWordCorrection(result, i);
                body = body.replaceAll(misspelledWord, replacement);
            }
            //Display a Toast saying how many words were replaced
            String wordsFixed = SpellCheck.getWordsFixedString(result.length());
        }
        //Update the EditText field with the fixed text
        if(noteEditorActivity != null){
            noteEditorActivity.setBody(body);
            noteEditorActivity.moveCursorToEnd();
        }
        else if(noteEditorFragment != null){
            noteEditorFragment.setBody(body);
            noteEditorFragment.moveCursorToEnd();
        }

    }
}