/*
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 2016-4-21
 * Description: This class tests the SpellCheck class and makes sure that it returns the correct string
 * depending on the number of words that were corrected.
 */

package com.ser210.cyr.clevernotes_sdk21;

import com.ser210.cyr.clevernotes2.HelperClasses.SpellCheck;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class SpellCheckTest {

    @Test
    public void testGetWordsFixedString() throws Exception {
        assertEquals("No Words Fixed", SpellCheck.getWordsFixedString(0));
        assertEquals("1 Word Fixed", SpellCheck.getWordsFixedString(1));
        assertEquals("2 Words Fixed", SpellCheck.getWordsFixedString(2));
    }

}