/*
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 2016-4-21
 * Description: This is used by the note editor activity and parses JSON received from the REST API
 * and formats Strings for use on the layouts.
 */

package com.ser210.cyr.clevernotes2.HelperClasses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

public class SpellCheck {

    //Gets the suggested spelling of an individual word from the JSONObject at an index
    public static String getMisspelledWordCorrection(JSONObject result, int index) {

        String replacement = "";

        try {
            String misspelledWord = result.names().get(index).toString().trim();
            JSONArray jsonArray = result.getJSONArray(misspelledWord);
            replacement = jsonArray.get(0).toString().replaceAll("[^a-zA-Z ]", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return replacement;
    }

    //Returns the misspelled word at a certain index in the JSON Data received from the REST API
    public static String getMisspelledWord(JSONObject result, int index) {

        String misspelledWord = "";

        try {
            misspelledWord = result.names().get(index).toString().trim();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return misspelledWord;
    }

    //Returns a full String from the BufferedReader
    public static String getStringFromBuffer(BufferedReader reader) {
        String line;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            return null;
        }
    }

    //Returns a String explaining how many words were fixed by the REST API
    //Is used as a Toast in the Note EditorActivity
    public static String getWordsFixedString(int numberOfWords) {
        if (numberOfWords == 0) {
            return "No Words Fixed";
        } else if (numberOfWords == 1) {
            return "1 Word Fixed";
        } else {
            return numberOfWords + " Words Fixed";
        }
    }
}
