/*
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 2016-4-21
 * Description: This class tests the Notes class and makes sure that it returns the correct variables.
 */

package com.ser210.cyr;

import com.ser210.cyr.clevernotes2.EntityClasses.Note;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NoteTest {

    //Predefined variables to pass to the Tag constructor
    String title = "title";
    String body = "body";
    String created = "today";
    String modified = "tomorrow";
    String tags = "tag1, tag2";

    //Create a new Tag object with the predefined variables
    Note note = new Note();

    @Before
    public void setUp() throws Exception {
        note.setId(0);
        note.setCreated(created);
        note.setFormattedDate(created);
        note.setModified(modified);
        note.setTitle(title);
        note.setBody(body);
        note.setTags(tags);
    }

    //Check to see if the Note object returns the correct title
    @Test
    public void getTitleTest() throws Exception {
        assertEquals(title, note.getTitle());
    }

    //Check to see if the Note object returns the correct body
    @Test
    public void getBodyTest() throws Exception {
        assertEquals(body, note.getBody());
    }

    //Check to see if the Note object returns the correct date created
    @Test
    public void getCreatedTest() throws Exception {
        assertEquals(created, note.getCreated());
    }

    //Check to see if the Note object returns the correct date modified
    @Test
    public void getModifiedTest() throws Exception {
        assertEquals(modified, note.getModified());
    }

    //Check to see if the Note object returns the correct tags
    @Test
    public void getTagsTest() throws Exception {
        assertEquals(tags, note.getTags());
    }

    //Check to see if the Note object returns the correct id
    @Test
    public void getIdTest() throws Exception {
        assertEquals(0, note.getId());
    }

}
