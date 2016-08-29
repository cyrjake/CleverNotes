/*
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 2016-4-21
 * Description: This class tests the KeywordExtraction class and makes sure that it returns the correct
 * formatted string.
 */

package com.ser210.cyr.clevernotes_sdk21;

import com.ser210.cyr.clevernotes2.HelperClasses.KeywordExtraction;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeywordExtractionTest {

    @Test
    public void testFormatStringForView() throws Exception {
        String[] string =  new String[]{"tag","another tag", "third tag"};
        assertEquals("Tag, Another Tag, Third Tag", KeywordExtraction.formatStringForView(string));
    }
}