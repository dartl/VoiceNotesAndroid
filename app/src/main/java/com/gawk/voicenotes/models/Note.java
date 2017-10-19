package com.gawk.voicenotes.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by GAWK on 10.02.2017.
 */

public class Note implements Serializable, Parcelable {
    private long id;
    private String text_note;
    private Date date;
    private long mCategoryId;

    public Note() {
        this.id = -1;
        this.text_note = null;
        this.date = null;
        this.mCategoryId = -1;
    }

    public Note(long _id, String _text_note, Date _date, long _categoryId) {
        this.id = _id;
        this.text_note = _text_note;
        this.date = _date;
        this.mCategoryId = _categoryId;
    }

    public Note(Cursor elem) {
        if (elem.getCount() > 1 || elem.moveToFirst()) {
            this.id = elem.getLong(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_ID));
            this.text_note = elem.getString(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE));
            long temp = elem.getLong(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE));
            this.date = new Date(temp);
            this.mCategoryId = elem.getLong(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_CATEGORY));
        } /*else if () {
            this.id = elem.getLong(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_ID));
            this.text_note = elem.getString(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE));
            long temp = elem.getLong(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE));
            this.date = new Date(temp);
            this.mCategoryId = elem.getLong(elem.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_CATEGORY));
        }*/
    }

    protected Note(Parcel in) {
        Note n = (Note) in.readSerializable();
        this.id = n.getId();
        this.text_note = n.getText_note();
        this.date = n.getDate();
        this.mCategoryId = n.getCategoryId();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText_note() {
        return text_note;
    }

    public void setText_note(String text_note) {
        this.text_note = text_note;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this);
    }

    public long getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(long mCategoryId) {
        this.mCategoryId = mCategoryId;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", text_note='" + text_note + '\'' +
                ", date=" + date +
                '}';
    }
}
