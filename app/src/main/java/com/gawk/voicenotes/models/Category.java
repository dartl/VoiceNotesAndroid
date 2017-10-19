package com.gawk.voicenotes.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by GAWK on 19.10.2017.
 */

public class Category implements Serializable, Parcelable {
    private long mId;
    private String mName;

    public Category() {
    }

    public Category(long mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }

    public Category(Cursor elem) {
        if (elem.getCount() > 1 || elem.moveToFirst()) {
            this.mId = elem.getLong(elem.getColumnIndex(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_ID));
            this.mName = elem.getString(elem.getColumnIndex(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_NAME));
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
}
