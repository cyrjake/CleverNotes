/*
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 28 April 2016
 * Description: This class parses JSON data from the Analysis Activity and formats String capitalization.
 */

package com.ser210.cyr.clevernotes2.HelperClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class Sentiment {

    //Gets the sentiment string result from the jsonobject
    public static String getSentimentType(JSONObject jsonObject) {
        String sentiment = " ";
        try {
            sentiment = StringFormatting.capFirstLetter(jsonObject.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sentiment;
    }

    //Returns the json numerical rating of the sentiment of the note body
    public static double getSentimentRating(JSONObject jsonObject) {

        try {
            return jsonObject.getDouble("score");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
