package com.gawk.voicenotes.models;

import android.content.Context;

import com.gawk.voicenotes.adapters.preferences.PrefUtil;

/**
 * Created by GAWK on 07.08.2017.
 */

public class Statistics {
    public static final long START_EXPERIENCE = 2;

    public static final String PREF_EXPERIENCE = "PREF_EXPERIENCE";
    public static final String PREF_BORDER_EXPERIENCE = "PREF_BORDER_EXPERIENCE";
    public static final String PREF_UP_EXPERIENCE = "PREF_UP_EXPERIENCE";
    public static final String PREF_LEVEL = "PREF_LEVEL";

    public static final String PREF_CREATE_NOTES = "PREF_CREATE_NOTES";
    public static final String PREF_CREATE_NOTIFICATIONS = "PREF_CREATE_NOTIFICATIONS";
    public static final String PREF_GET_NOTIFICATIONS = "PREF_GET_NOTIFICATIONS";
    public static final String PREF_REMOVE_NOTES = "PREF_REMOVE_NOTES";
    public static final String PREF_EXPORTS = "PREF_EXPORTS";
    public static final String PREF_IMPORTS = "PREF_IMPORTS";

    public static final long POINT_CREATE_NOTES = 2;
    public static final long POINT_CREATE_NOTIFICATIONS = 1;
    public static final long POINT_GET_NOTIFICATIONS = 1;
    public static final long POINT_REMOVE_NOTES = 1;
    public static final long POINT_EXPORTS = 2;
    public static final long POINT_IMPORTS = 2;

    private PrefUtil mPrefUtil;

    public Statistics(Context context) {
        mPrefUtil = new PrefUtil(context);
    }

    public void upExperience(long code) {
        long newExperiences = mPrefUtil.getLong(PREF_EXPERIENCE,0);
        newExperiences += code;
        long borderExperiences = mPrefUtil.getLong(PREF_BORDER_EXPERIENCE,0);
        long upExperiences = mPrefUtil.getLong(PREF_UP_EXPERIENCE,START_EXPERIENCE);
        if (newExperiences - borderExperiences >= upExperiences) {
            borderExperiences = upExperiences;
            upExperiences = (long) Math.ceil(upExperiences*1.2);
            int level = mPrefUtil.getInt(PREF_LEVEL,0);
            mPrefUtil.saveInt(PREF_LEVEL,++level);
        }
        mPrefUtil.saveLong(PREF_EXPERIENCE,newExperiences);
        mPrefUtil.saveLong(PREF_BORDER_EXPERIENCE,borderExperiences);
        mPrefUtil.saveLong(PREF_UP_EXPERIENCE,upExperiences);
    }

    public void addPointCreateNotes() {
        long createNotes = mPrefUtil.getLong(PREF_CREATE_NOTES,0);
        mPrefUtil.saveLong(PREF_CREATE_NOTES,++createNotes);
        upExperience(POINT_CREATE_NOTES);
    }

    public void addPointCreateNotifications() {
        long createNotification = mPrefUtil.getLong(PREF_CREATE_NOTIFICATIONS,0);
        mPrefUtil.saveLong(PREF_CREATE_NOTIFICATIONS,++createNotification);
        upExperience(POINT_CREATE_NOTIFICATIONS);
    }

    public void addPointGetNotifications() {
        long getNotification = mPrefUtil.getLong(PREF_GET_NOTIFICATIONS,0);
        mPrefUtil.saveLong(PREF_GET_NOTIFICATIONS,++getNotification);
        upExperience(POINT_GET_NOTIFICATIONS);
    }

    public void addPointRemoveNotes() {
        long removeNotes = mPrefUtil.getLong(PREF_REMOVE_NOTES,0);
        mPrefUtil.saveLong(PREF_REMOVE_NOTES,++removeNotes);
        upExperience(POINT_REMOVE_NOTES);
    }

    public void addPointExports() {
        long exports = mPrefUtil.getLong(PREF_EXPORTS,0);
        mPrefUtil.saveLong(PREF_EXPORTS,++exports);
        upExperience(POINT_EXPORTS);
    }

    public void addPointImports() {
        long exports = mPrefUtil.getLong(PREF_IMPORTS,0);
        mPrefUtil.saveLong(PREF_IMPORTS,++exports);
        upExperience(POINT_IMPORTS);
    }

    public long getExperience() {
        return mPrefUtil.getLong(PREF_EXPERIENCE,0);
    }

    public long getBorderExperience() {
        return mPrefUtil.getLong(PREF_BORDER_EXPERIENCE,0);
    }

    public long getUpExperience() {
        return mPrefUtil.getLong(PREF_UP_EXPERIENCE,START_EXPERIENCE);
    }

    public int getLevel() {
        return mPrefUtil.getInt(PREF_LEVEL,0);
    }

    public long getCreateNotes() {
        return mPrefUtil.getLong(PREF_CREATE_NOTES,0);
    }

    public long getCreateNotifications() {
        return mPrefUtil.getLong(PREF_CREATE_NOTIFICATIONS,0);
    }

    public long getGetNotifications() {
        return mPrefUtil.getLong(PREF_GET_NOTIFICATIONS,0);
    }

    public long getRemoveNotes() {
        return mPrefUtil.getLong(PREF_REMOVE_NOTES,0);
    }

    public long getExports() {
        return mPrefUtil.getLong(PREF_EXPORTS,0);
    }

    public long getImports() {
        return mPrefUtil.getLong(PREF_IMPORTS,0);
    }
}
