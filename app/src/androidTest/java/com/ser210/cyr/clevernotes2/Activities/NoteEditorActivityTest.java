/*
* Created By: Jake Cyr and Yishuo Tang
* Date: 5/5/16
* Description: This class tests the UI of the app
*/

package com.ser210.cyr.clevernotes2.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.ser210.cyr.clevernotes2.HelperClasses.Constants;
import com.ser210.cyr.clevernotes2.HelperClasses.DatabaseHelper;
import com.ser210.cyr.clevernotes_sdk21.R;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;

public class NoteEditorActivityTest {

    @Rule
    public ActivityTestRule<NoteEditorActivity> mActivityRule = new ActivityTestRule<>(
            NoteEditorActivity.class);

    //Test setting the body and calling the API to get tags
    @Test
    public void testGetTags() throws Exception {
        onView(withId(R.id.body)).check(matches(isDisplayed()));

        onView(withId(R.id.title_edit_text))
                .perform(typeText("Note title"), closeSoftKeyboard());

        // Type text and then press the button.
        onView(withId(R.id.body))
                .perform(typeText("This is a new note."), closeSoftKeyboard());

        onView(withId(R.id.tags_input)).perform(typeText("Tag"), closeSoftKeyboard());

        onView(withId(R.id.save)).perform(click());

        DatabaseHelper databaseHelper = new DatabaseHelper(getTargetContext(), Constants.NOTES_TABLE, null, 1);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.NOTES_TABLE + " WHERE " + Constants.KEY_TITLE + " LIKE '%Note title%'", null);
        cursor.moveToFirst();

        String title = cursor.getString(cursor.getColumnIndex(Constants.KEY_TITLE));

        // Check that the text was changed.
        assertEquals("Note title", title);
    }

    /**
     * Perform action of waiting for a specific time.
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific time: " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;

                while (System.currentTimeMillis() < endTime) ;
            }
        };
    }
}
