package com.gawk.voicenotes.models;

/**
 * Created by GAWK on 07.08.2017.
 */

public class Statistics {
    // контастны значений полей статистики для БД
    public static final String DB_EXPERIENCE = "EXPERIENCE";
    public static final String DB_UP_EXPERIENCE = "UP_EXPERIENCE";
    public static final String DB_LEVEL = "LEVEL";

    public static final String DB_CREATE_NOTES = "CREATE_NOTES";
    public static final String DB_CREATE_NOTIFICATIONS = "CREATE_NOTIFICATIONS";
    public static final String DB_GET_NOTIFICATIONS = "GET_NOTIFICATIONS";
    public static final String DB_REMOVE_NOTES = "REMOVE_NOTES";
    public static final String DB_EXPORTS = "EXPORTS";
    public static final String DB_IMPORTS = "IMPORTS";

    public static final long POINT_CREATE_NOTES = 2;
    public static final long POINT_CREATE_NOTIFICATIONS = 1;
    public static final long POINT_GET_NOTIFICATIONS = 1;
    public static final long POINT_REMOVE_NOTES = 1;
    public static final long POINT_EXPORTS = 2;
    public static final long POINT_IMPORTS = 2;

    private long mExperience;
    private long mBorderExperience;
    private long mUpExperience;
    private int mLevel;
    // метрики
    private long mCreateNotes;
    private long mCreateNotifications;
    private long mGetNotifications;
    private long mRemoveNotes;
    private long mExports;
    private long mImports;
}
