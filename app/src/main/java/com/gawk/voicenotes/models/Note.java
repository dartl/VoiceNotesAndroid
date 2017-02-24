package com.gawk.voicenotes.models;

import android.database.Cursor;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by GAWK on 10.02.2017.
 */

public class Note implements Serializable {
    private int id;
    private String text_note;
    private Date date;

    public Note() {
        this.id = -1;
        this.text_note = null;
        this.date = null;
    }

    public Note(int _id, String _text_note, Date _date) {
        this.id = _id;
        this.text_note = _text_note;
        this.date = _date;
    }

    public Note(Cursor elem) {
        this.id = elem.getInt(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_ID));
        this.text_note = elem.getString(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE));
        int temp = elem.getInt(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE));
        this.date = new Date(temp);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText_note() {
        return text_note;
    }

    public void setText_note(String text_note) {
        this.text_note = text_note;
    }
}
