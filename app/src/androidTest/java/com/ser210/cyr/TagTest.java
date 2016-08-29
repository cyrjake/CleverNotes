/*
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 2016-4-21
 * Description: This class tests the Tags class and makes sure that it returns the correct variables.
 */

package com.ser210.cyr;

import com.ser210.cyr.clevernotes2.EntityClasses.Tag;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TagTest {

    //Predefined variables to pass to the Tag constructor
    String name = "TAGNAME";
    String dateModified = "2016-4-21";

    //Create a new Tag object with the predefined variables
    Tag tag = new Tag();

    @Before
    public void setUp() throws Exception {
        tag.setTag(name);
        tag.setCreated(dateModified);
    }

    //Check to see if the Tag object returns the correct name
    @Test
    public void testGetName() throws Exception {
        assertEquals(name, tag.getTag());
    }

    //Check to see if the Tag object returns the correct modification date
    @Test
    public void testGetDateModified() throws Exception {
        assertEquals(dateModified, tag.getCreated());
    }

}