package com.gawk.voicenotes.models;

import android.database.Cursor;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GAWK on 10.02.2017.
 */

public class Notification implements Serializable {
    private int id, id_note;
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
        this.id = element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID));
        this.id_note = element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID));
        String s = element.getString(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE));
        this.sound = toBoolean(element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_SOUND)));
        this.shake = toBoolean(element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_VIBRATE)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try
        {
            Date date = simpleDateFormat.parse(s);
            System.out.println("date : "+simpleDateFormat.format(date));
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }
        this.date = null;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_note() {
        return id_note;
    }

    public void setId_note(int id_note) {
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
