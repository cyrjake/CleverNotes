/**
 * Authors: Jake Cyr and Yishuo Tang
 * Date: 27 April 2016
 * Description: This class allows the screen size of the device being used to be determined
 */


package com.ser210.cyr.clevernotes2.HelperClasses;

import android.app.Activity;
import android.content.res.Configuration;

public class ScreenSize {

    public static boolean isLargeScreen(Activity activity){
        int screenSize = activity.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE){
            return true;
        }
        else{
            return false;
        }
    }
}
