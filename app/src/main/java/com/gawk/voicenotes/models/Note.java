package com.gawk.voicenotes.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        this.date = Calendar.getInstance().getTime();
        this.mCategoryId = -1;
    }

    public Note(long _id, String _text_note, Date _date) {
        this(_id, _text_note, _date,-1);
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
        } else {
            this.id = -1;
            this.text_note = "";
            this.date = new Date();
            this.mCategoryId = -1;
        }
    }

    public Note(JSONObject objNote) {
        String date;
        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);

        this.id = -1;
        try {
            this.text_note = objNote.getString(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE);
            date = objNote.getString(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE);
            this.date = dateFormat.parse(date);
            this.mCategoryId = objNote.getLong(SQLiteDBHelper.NOTES_TABLE_COLUMN_CATEGORY);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
                ", mCategoryId=" + mCategoryId +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject obj;
        obj = new JSONObject();
        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
        try {
            obj.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, this.text_note);
            obj.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, dateFormat.format(this.date));
            obj.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_CATEGORY, this.mCategoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
