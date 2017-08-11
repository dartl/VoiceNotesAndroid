package com.gawk.voicenotes.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by GAWK on 03.07.2017.
 */

public class PrefUtil {
    // константы названий настроек
    public static final String NOTIFICATION_SOUND = "sound_for_notification";

    // основной код
    public static final String PERSISTANT_STORAGE_NAME = "GAWK_VOICE_NOTES";
    private SharedPreferences sharedPreferences;

    public PrefUtil(SharedPreferences _sp) {
        sharedPreferences = _sp;
    }

    public PrefUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(PERSISTANT_STORAGE_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key, String def) {
        return sharedPreferences.getString(key,def);
    }

    public boolean saveString(String key, String value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(key, value);
        return ed.commit();
    }

    public boolean saveInt(String key, int value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt(key, value);
        return ed.commit();
    }

    public int getInt(String key, int def) {
        return sharedPreferences.getInt(key,def);
    }

    public boolean saveBoolean(String key, boolean value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putBoolean(key, value);
        return ed.commit();
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedPreferences.getBoolean(key,def);
    }

    public boolean saveLong(String key, long value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putLong(key, value);
        return ed.commit();
    }

    public long getLong(String key, long def) {
        return sharedPreferences.getLong(key,def);
    }
}
