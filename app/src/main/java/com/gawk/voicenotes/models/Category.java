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
import java.util.Date;
import java.util.Locale;

/**
 * Created by GAWK on 19.10.2017.
 */

public class Category implements Serializable, Parcelable {
    private long mId;
    private String mName;

    public Category() {
        this.mId = -1;
        this.mName = "";
    }

    public Category(long mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }

    public Category(Cursor elem) {
        if (elem.getCount() > 1 || elem.moveToFirst()) {
            this.mId = elem.getLong(elem.getColumnIndex(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_ID));
            this.mName = elem.getString(elem.getColumnIndex(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_NAME));
        } else {
            this.mId = -1;
            this.mName = "";
        }
    }

    public Category(JSONObject objNote) {
        try {
            this.mId = objNote.getLong(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_ID);
            this.mName = objNote.getString(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Category(Parcel in) {
        Category n = (Category) in.readSerializable();
        this.mId = n.getId();
        this.mName = n.getName();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(this);
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public JSONObject toJSON() {
        JSONObject obj;
        obj = new JSONObject();
        try {
            obj.put(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_ID, this.mId);
            obj.put(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_NAME, this.mName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
