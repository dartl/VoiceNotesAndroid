package com.gawk.voicenotes.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by GAWK on 08.06.2017.
 */

public class UtilPreferences {
    public static final String CHECK_RECOGNITION = "checkRecognition"; // key для хранения bool значения проверки возможности распознования

    public UtilPreferences() {}

    public static boolean isNotEmptyString(String name, Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(activity);
        if (sPref.getString("name", "").equalsIgnoreCase("")) {
            return false;
        }
        return true;
    }

    public static boolean getBoolean(String name, Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(activity);
        return sPref.getBoolean(name,true);
    }

    public static boolean setBoolean(String name, boolean b,  Activity activity) {
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(name,b);
        return ed.commit();
    }
}
