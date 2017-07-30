package com.intimealarm.findme.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import static android.content.Context.MODE_PRIVATE;

/**
 * @author Conor Keenan
 * Student No: x13343806
 * Created on 20/02/2017.
 */

public class Helper {

    public Helper(){
    }

    // Method to check if all fields have been completed
    public boolean checkFields(String... fields){
        boolean isValid = true;

        for (String x : fields) {
            if (TextUtils.isEmpty(x)) {
                isValid = false;
            }
        }

        return isValid;
    }

    public void disableSMS(Context c){
        SharedPreferences sharedPref = c.getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Constants.SHARED_SMS_ENABLED, false);
        editor.apply();
    }

}
