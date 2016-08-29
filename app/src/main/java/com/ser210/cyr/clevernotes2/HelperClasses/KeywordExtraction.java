/*
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 2016-4-21
 * Description: This class parses JSON data for the Analysis Activity and formats Strings to be used
 * in the layouts
 */

package com.ser210.cyr.clevernotes2.HelperClasses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KeywordExtraction {

    //This method retrieves the keywords from the referenced JSONObject and returns a String array of them
    public static String[] getKeywordsFromJSON(JSONObject jsonObject) {
        String[] words;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("keywords");
            words = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                words[i] = StringFormatting.capAllWords(jsonArray.getJSONObject(i).getString("text"));
            }
            return words;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Formats a string array into a comma separated list with all capitalized words
    public static String formatStringForView(String[] keywords) {

        if (keywords != null) {
            if (keywords.length > 0) {
                String temp = StringFormatting.capAllWords(keywords[0]);

                for (int i = 1; i < keywords.length; i++) {
                    temp = temp + ", " + StringFormatting.capAllWords(keywords[i]);
                }
                return temp;
            } else {
                return "No keywords found";
            }
        }
        else{
            return "No keywords found";
        }
    }
}
