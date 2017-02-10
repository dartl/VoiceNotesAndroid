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
    private int id;
    //private String path_file;
    private int id_note;
    private Date date;

    public Notification() {
        this.id = -1;
        this.id_note = -1;
        this.date = null;
    }

    public Notification(Cursor element) {
        this.id = element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID));
        this.id_note = element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID));
        String s = element.getString(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_DATE));
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

    public Notification(int id, int id_note, Date date) {
        this.id = id;
        this.id_note = id_note;
        this.date = date;
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
}
