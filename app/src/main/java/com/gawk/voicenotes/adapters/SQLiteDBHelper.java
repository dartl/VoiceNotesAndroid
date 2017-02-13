package com.gawk.voicenotes.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.gawk.voicenotes.models.Note;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by GAWK on 10.02.2017.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static SQLiteDBHelper sInstance;
    private SQLiteDatabase db;

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "VOICE_NOTES.DB";

    public static final String NOTES_TABLE_NAME = "NOTES";
    public static final String NOTES_TABLE_COLUMN_ID = "_id";
    public static final String NOTES_TABLE_COLUMN_TEXT_NOTE = "TEXT_NOTE";
    public static final String NOTES_TABLE_COLUMN_DATE = "DATE";

    public static final String NOTIFICATIONS_TABLE_NAME = "NOTIFICATIONS";
    public static final String NOTIFICATIONS_TABLE_COLUMN_ID = "_id";
    public static final String NOTIFICATIONS_TABLE_COLUMN_ID_NOTE = "ID_NOTE";
    public static final String NOTIFICATIONS_TABLE_COLUMN_ID_DATE = "DATE";

    public static synchronized SQLiteDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public SQLiteDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(
                "create table " + NOTES_TABLE_NAME +
                        "(" + NOTES_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTES_TABLE_COLUMN_TEXT_NOTE + " text, " + NOTES_TABLE_COLUMN_DATE + " text)"
        );
        db.execSQL(
                "create table " + NOTIFICATIONS_TABLE_NAME +
                        "(" + NOTIFICATIONS_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTIFICATIONS_TABLE_COLUMN_ID_NOTE + " integer, " + NOTIFICATIONS_TABLE_COLUMN_ID_DATE + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion != oldVersion) {
            db.execSQL(
                    "create table " + NOTIFICATIONS_TABLE_NAME +
                            "(" + NOTIFICATIONS_TABLE_COLUMN_ID + " integer primary key, " +
                            NOTIFICATIONS_TABLE_COLUMN_ID_NOTE + " integer, " + NOTIFICATIONS_TABLE_COLUMN_ID_DATE + " text)"
            );
        }
    }

    // Подключаемся к БД
    public void connection() {
        try {
            db = sInstance.getWritableDatabase();
        }
        catch (SQLiteException ex){
        }
    }

    // Отключиться от БД
    public void disconnection() {
        try {
            db.close();
            db = null;
        }
        catch (SQLiteException ex){
        }
    }

    /*
        Методы для работы с Note
     */

    // Получить указатель на все заметки
    public Cursor getCursorAllNotes() {
        if (!db.isOpen()) {
            return null;
        }
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE
                + " DESC", null);
    }

    // Сохранить новую заметку или обновить существующую
    // action = 0 - добавить новую; action = 1 - обновить существующую
    public boolean saveNote(Note note, int action) {
        if (!db.isOpen()) {
            return false;
        }
        ContentValues newValues = new ContentValues();
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, note.getText_note());
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, note.getDate());
        switch (action) {
            case 0:
                db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
                return true;
            case 1:
                db.update(SQLiteDBHelper.NOTES_TABLE_NAME, newValues, "id = ?",
                    new String[] { String.valueOf(note.getId()) });
                return true;
            default:
                return false;
        }
    }

    public boolean importDB(File file, SQLiteDatabase db) {
        String json = "";
        if (file.canRead()) {
            try {
                FileInputStream fin = new FileInputStream(file);
                json = convertStreamToString(fin);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject jreader = new JSONObject(json);
                if (jreader.toString().length() >= 1) {
                    JSONObject objNote;
                    String textNote;
                    String date;
                    JSONArray arrayNames = jreader.names();
                    JSONArray array = jreader.toJSONArray(arrayNames);
                    for (int  i = 0; i < array.length(); i++) {
                        objNote=(JSONObject)array.get(i);
                        textNote = objNote.getString(NOTES_TABLE_COLUMN_TEXT_NOTE);
                        date = objNote.getString(NOTES_TABLE_COLUMN_DATE);
                        if (textNote != null && date != null) {
                            ContentValues newValues = new ContentValues();
                            newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, textNote);
                            newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, date);
                            db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
                        } else {
                            return false;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public boolean exportDB(File file, SQLiteDatabase db) {
        JSONObject obj;
        JSONObject resultJson = new JSONObject();
        Note note;
        Cursor cursorObj = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTES_TABLE_COLUMN_ID
                + " DESC", null);
        while (cursorObj.moveToNext()) {
            note = new Note(cursorObj);
            obj = new JSONObject();
            try {
                obj.put(NOTES_TABLE_COLUMN_TEXT_NOTE, note.getText_note());
                obj.put(NOTES_TABLE_COLUMN_DATE, note.getDate());
                resultJson.put(String.valueOf(note.getId()),obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (file.canWrite()) {
            try {
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.println(resultJson.toString());
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

}
