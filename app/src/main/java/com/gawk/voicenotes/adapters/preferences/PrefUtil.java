package com.gawk.voicenotes.adapters.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.gawk.voicenotes.adapters.subs.SubsInterface;

import java.util.Set;

/**
 * Created by GAWK on 03.07.2017.
 */

public class PrefUtil {
    // константы названий настроек
    public static final String INFORMATION_ABOUT_PERMISSIONS = "information_about_permissions";
    public static final String NOTIFICATION_SOUND = "sound_for_notification";
    public static final String NOTIFICATION_INTERVAL = "interval_for_notification";
    public static final String NOTE_AUTO_SAVE = "note_auto_save";
    public static final String THEME = "theme_voice_notes";
    public static final String DONATE_SHOW = "donate_show";
    public static final String SUPPORTED_LANGUAGE_FOR_RECOGNIZE = "supported_language_for_recognize";
    public static final String SELECTED_LANGUAGE_FOR_RECOGNIZE = "selected_language_for_recognize";
    public static final String FONT_SIZE = "font_size";

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

    public boolean saveStringSet(String key, Set<String> set) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putStringSet(key,set);
        return ed.commit();
    }

    public Set<String> getStringSet(String key, Set<String> set) {
        return sharedPreferences.getStringSet(key, set);
    }

    public long getLong(String key, long def) {
        return sharedPreferences.getLong(key,def);
    }

    public int subsGetActive() {
        return sharedPreferences.getInt(SubsInterface.DONATE_PREF,0);
    }
}
