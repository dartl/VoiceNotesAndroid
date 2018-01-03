package com.gawk.voicenotes.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by GAWK on 10.02.2017.
 */

public class Notification implements Serializable, Parcelable {
    private long id, id_note;
    private Date date;
    private boolean sound, shake, mRepeat;

    public Notification() {
        this.id = -1;
        this.id_note = -1;
        this.date = new Date();
        this.sound = true;
        this.shake = true;
        this.mRepeat = false;
    }

    public Notification(Cursor element) {
        if (element.getCount() > 1 || element.moveToFirst()) {
            this.id = element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID));
            this.id_note = element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE));
            this.sound = toBoolean(element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_SOUND)));
            this.shake = toBoolean(element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_VIBRATE)));
            this.mRepeat = toBoolean(element.getInt(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_REPEAT)));
            this.date = new Date(element.getLong(element.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE)));
        } else {
            this.id = -1;
            this.id_note = -1;
            this.date = null;
            this.sound = true;
            this.shake = true;
            this.mRepeat = false;
        }
    }

    public Notification(long id, long id_note, Date date, boolean sound, boolean shake, boolean repeat) {
        this.id = id;
        this.id_note = id_note;
        this.date = date;
        this.sound = sound;
        this.shake = shake;
        this.mRepeat = repeat;
    }

    public Notification(long id, long id_note, Date date, int sound, int shake, int repeat) {
        this.id = id;
        this.id_note = id_note;
        this.date = date;
        this.sound = toBoolean(sound);
        this.shake = toBoolean(shake);
        this.mRepeat = toBoolean(repeat);
    }

    protected Notification(Parcel in) {
        Notification notification = (Notification) in.readSerializable();
        id = notification.getId();
        id_note = notification.getId_note();
        sound = notification.isSound();
        shake = notification.isShake();
        date = notification.getDate();
        mRepeat = notification.isRepeat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

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

    public boolean isRepeat() {
        return mRepeat;
    }

    public void setRepeat(boolean mRepeat) {
        this.mRepeat = mRepeat;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", id_note=" + id_note +
                ", date=" + date +
                ", sound=" + sound +
                ", shake=" + shake +
                ", mRepeat=" + mRepeat +
                '}';
    }
}
