/*
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 2016-4-21
 * Description: This class formats Strings for use in various classes/activities of the app
 */

package com.ser210.cyr.clevernotes2.HelperClasses;

import android.util.Log;

import com.ser210.cyr.clevernotes2.EntityClasses.Tag;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class StringFormatting {

    //Capitalize the first letter of a word
    public static String capitalizeFirstLetter(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    //Capitalizes the first letter of a word (For longer words)
    public static String capFirstLetter(String word) {
        if (word.length() > 1) {
            String temp = String.valueOf(word.charAt(0)).toUpperCase();
            word = word.substring(1, word.length()).toLowerCase();
            word = temp + word;
        }
        return word;
    }

    //Capitalizes the first letter of all words in the referenced string
    public static String capAllWords(String sentence) {
        String[] words = sentence.split(" ");
        String newSentence = capFirstLetter(words[0]);
        for (int i = 1; i < words.length; i++) {
            if (words[i].length() > 1) {
                newSentence = newSentence + " " + capFirstLetter(words[i]);
            } else {
                newSentence = newSentence + " " + words[i];
            }
        }
        return newSentence;
    }

    //Get the date and time
    public static String getDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Log.v("Get", df.format(c.getTime()) + " , " + getTime());
        return df.format(c.getTime()) + " , " + getTime();
    }

    //Get the time
    public static String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    //Get the date formatted nicely for display
    public static String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");

        Log.v("Get", df.format(c.getTime()));
        return df.format(c.getTime());
    }

    //Prepare a string for use with the API by replacing all spaces with +
    public static String prepareStringForAPI(String string) {

        if (string.contains(".")) {
            String[] sentences = string.split(".");

            if (sentences.length >= 2) {
                String firstTwoSentences = sentences[0] + " " + sentences[1];
                firstTwoSentences = removePunctuation(firstTwoSentences).replaceAll(" ", "+");
                return firstTwoSentences;
            } else if (sentences.length == 1) {
                return removePunctuation(sentences[0]).replaceAll(" ", "+");
            } else {
                return removePunctuation(string).replaceAll(" ", "+");
            }
        } else if (string.length() > 30) {
            return removePunctuation(string).replaceAll(" ", "+");
        } else {
            return removePunctuation(string).replaceAll(" ", "+");
        }
    }

    //Remove all punctuation in a String
    public static String removePunctuation(String string) {
        return string.replaceAll("[^a-zA-Z ]", "").trim();
    }

    public static String listToCSString(List<Tag> list){
        String listString = "";

        if(list != null){

            for (Tag s : list)
            {
                listString += s.getTag() + ", ";
            }
        }
        return listString;
    }
}
