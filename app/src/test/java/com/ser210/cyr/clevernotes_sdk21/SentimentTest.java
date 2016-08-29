package com.ser210.cyr.clevernotes_sdk21;

import com.ser210.cyr.clevernotes2.HelperClasses.StringFormatting;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SentimentTest {

    @Test
    public void prepareStringForAPI() throws Exception{
        String string = "This is an ";
        assertEquals("This+is+an", StringFormatting.prepareStringForAPI(string));
    }

    @Test
    public void removePuncutation() throws Exception{
        String string = "\"This is, a. quote\"";
        assertEquals("This is a quote", StringFormatting.removePunctuation(string));
    }

    @Test
    public void prepareStringForAPIANDRemovePunctuation() throws Exception{
        String string = "American advisory force in Syria, U.S. officials said Sunday,";
        String ready = "American+advisory+force+in+Syria+US+officials+said+Sunday";
        assertEquals(ready, StringFormatting.prepareStringForAPI(string));
    }

}