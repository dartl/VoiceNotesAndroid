package com.gawk.voicenotes.models;

import android.database.Cursor;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by GAWK on 10.02.2017.
 */

public class Notification implements Serializable {
    private long id, id_note;
    private Date date;
    private boolean sound, shake;

    public Notification() {
        this.id = -1;
        this.id_note = -1;
        this.date = null;
        this.sound = true;
        this.shake = true;
    }

    public Notification(Cursor element) {
        element.moveToFirst();
        this.id = element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID));
        this.id_note = element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID));
        this.sound = toBoolean(element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_SOUND)));
        this.shake = toBoolean(element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_VIBRATE)));
        long temp = element.getLong(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE));
        this.date = new Date(temp);
    }

    public Notification(int id, int id_note, Date date, boolean sound, boolean shake) {
        this.id = id;
        this.id_note = id_note;
        this.date = date;
        this.sound = sound;
        this.shake = shake;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isShake() {
        return shake;
    }

    public void setShake(boolean shake) {
        this.shake = shake;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId_note() {
        return id_note;
    }

    public void setId_note(long id_note) {
        this.id_note = id_note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static boolean toBoolean (int pVal) {
        return pVal != 0;
    }
}
