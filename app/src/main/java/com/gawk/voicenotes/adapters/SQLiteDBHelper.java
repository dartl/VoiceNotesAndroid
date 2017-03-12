package com.gawk.voicenotes.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Адаптер для подключения к БД
 * @author GAWK
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
    public static final String NOTIFICATIONS_TABLE_COLUMN_DATE = "DATE";
    public static final String NOTIFICATIONS_TABLE_COLUMN_SOUND = "SOUND";
    public static final String NOTIFICATIONS_TABLE_COLUMN_VIBRATE = "VIBRATE";

    /**
     * Метод для получения ссылки на статический класс
     */
    public static synchronized SQLiteDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SQLiteDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Стандартный конструктор для получения экземпляра класса
     */
    public SQLiteDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Создание БД, если её ещё нет
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Создаем таблицу заметок
        db.execSQL(
                "create table " + NOTES_TABLE_NAME +
                        "(" + NOTES_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTES_TABLE_COLUMN_TEXT_NOTE + " text, " + NOTES_TABLE_COLUMN_DATE + " integer)"
        );
        // Создаем таблицу оповещений
        db.execSQL(
                "create table " + NOTIFICATIONS_TABLE_NAME +
                        "(" + NOTIFICATIONS_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTIFICATIONS_TABLE_COLUMN_ID_NOTE + " integer, " + NOTIFICATIONS_TABLE_COLUMN_DATE + " text, "
                        + NOTIFICATIONS_TABLE_COLUMN_SOUND + " integer, " + NOTIFICATIONS_TABLE_COLUMN_VIBRATE + " integer)"
        );
    }

    /**
     * Обновление БД, если версия другая
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Если версия 2, то
        if (newVersion > oldVersion) {
            // Создаем таблицу оповещений
            db.execSQL(
                    "create table " + NOTIFICATIONS_TABLE_NAME +
                            "(" + NOTIFICATIONS_TABLE_COLUMN_ID + " integer primary key, " +
                            NOTIFICATIONS_TABLE_COLUMN_ID_NOTE + " integer, " + NOTIFICATIONS_TABLE_COLUMN_DATE + " text, "
                            + NOTIFICATIONS_TABLE_COLUMN_SOUND + " integer, " + NOTIFICATIONS_TABLE_COLUMN_VIBRATE + " integer)"
            );

            // Обновляем таблицу заметок
        }
    }

    // Подключаемся к БД
    public void connection() {
        if (db == null) {
            try {
                db = sInstance.getWritableDatabase();
            } catch (SQLiteException ex) {
            }
        }
    }

    // Отключиться от БД
    public void disconnection() {
        if (db != null) {
            try {
                db.close();
                db = null;
            } catch (SQLiteException ex) {
            }
        }
    }

    /*
        Методы для работы с Notification
     */

    /**
     * Получаем указатель на список всех оповещений
     * @return возвращает результат запроса к БД
     */
    public Cursor getCursorAllNotification() {
        if (!db.isOpen()) {
            return null;
        }
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE
                + " ASC", null);
    }

    /**
     * Получаем указатель на список всех оповещений, привязанных к заметке
     * @param id идентификатор заметки
     * @return возвращает результат запроса к БД
     */
    public Cursor getAllNotificationByNote(long id) {
        if (!db.isOpen()) {
            return null;
        }
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME + " WHERE " + SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE
                + " = " + id, null);
    }

    /**
     * Удаляет все оповещения, привязанные к заметке
     * @param id идентификатор заметки
     * @return результат удаления
     */
    public boolean deleteAllNotificationByNote(long id) {
        if (!db.isOpen()) {
            return false;
        }
        // Получаем список всех оповещений для этой заметки
        Cursor deleteNotifications = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME + " WHERE " + SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE
                + " = " + id, null);
        // Поочередно удаляем все оповещения заметки
        deleteNotifications.moveToFirst();
        while (deleteNotifications.moveToNext()) {
            if (
                    !deleteNotification(
                            deleteNotifications.getLong(
                                    deleteNotifications.getColumnIndex(NOTIFICATIONS_TABLE_COLUMN_ID)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Сохранить новую заметку или обновить существующую
     * <p><b>action = 0</b> - добавить новую; <b>action = 1</b> - обновить существующую</p>
     * @param notification экземпляр оповещения
     * @param action действие для метода
     * @return возвращает результат запроса к БД
     */
    public long saveNotification(Notification notification, int action) {
        if (!db.isOpen()) {
            return -1;
        }
        ContentValues newValues = new ContentValues();
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE, notification.getId_note());
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE, notification.getDate().getTime());
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_SOUND, notification.isSound());
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_VIBRATE, notification.isShake());
        switch (action) {
            case 0:
                return db.insert(SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME, null, newValues);
            case 1:
                db.update(SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME, newValues, NOTIFICATIONS_TABLE_COLUMN_ID +" = ?",
                        new String[] { String.valueOf(notification.getId()) });
                return 0;
            default:
                return -1;
        }
    }

    /**
     * Сохранить новую заметку или обновить существующую
     * <p><b>action = 0</b> - добавить новую; <b>action = 1</b> - обновить существующую</p>
     * @param id идентификатор удаляемого оповещения
     * @return результат удаления
     */
    public boolean deleteNotification(long id) {
        int deleteRow = db.delete(SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME, "_id = ?" ,new String[] { String.valueOf(id) });
        if (deleteRow == 1) {
            return true;
        }
        return false;
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

    public Cursor getNoteById(long id){
        if (!db.isOpen()) {
            return null;
        }
        Cursor c = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " WHERE "+NOTES_TABLE_COLUMN_ID+" = " + id, null);
        c.moveToFirst();
        return c;
    }

    // Сохранить новую заметку или обновить существующую
    // action = 0 - добавить новую; action = 1 - обновить существующую
    public long saveNote(Note note, int action) {
        if (!db.isOpen()) {
            return -1;
        }
        ContentValues newValues = new ContentValues();
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, note.getText_note());
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, note.getDate().getTime());
        switch (action) {
            case 0:
                long i = db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
                Log.e("GAWK_ERR","addNote - " + String.valueOf(i));
                return i;
            case 1:
                db.update(SQLiteDBHelper.NOTES_TABLE_NAME, newValues, NOTES_TABLE_COLUMN_ID +" = ?",
                    new String[] { String.valueOf(note.getId()) });
                return note.getId();
            default:
                return -1;
        }
    }

    public boolean noteDelete(long id) {
        int deleteRow = db.delete(SQLiteDBHelper.NOTES_TABLE_NAME, SQLiteDBHelper.NOTES_TABLE_COLUMN_ID + " = ?" ,new String[] { String.valueOf(id) });
        if (deleteRow == 1) {
            Cursor cursorNotification = db.rawQuery("SELECT * FROM " +
                    SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME + " WHERE "+NOTIFICATIONS_TABLE_COLUMN_ID_NOTE+" = " + id, null);
            while (cursorNotification.moveToNext()) {
                if (
                            deleteNotification(
                                    cursorNotification.getLong(
                                            cursorNotification.getColumnIndex(NOTIFICATIONS_TABLE_COLUMN_ID)
                                    )
                            )
                        ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean importDB(File file) {
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
                JSONArray jreader = new JSONArray(json);
                if (jreader.toString().length() >= 1) {
                    JSONObject objNote;
                    String textNote, date;
                    DateFormat dateFormat;
                    dateFormat = SimpleDateFormat.getDateTimeInstance();
                    for (int  i = 0; i < jreader.length(); i++) {
                        objNote=(JSONObject)jreader.get(i);
                        textNote = objNote.getString(NOTES_TABLE_COLUMN_TEXT_NOTE);
                        date = objNote.getString(NOTES_TABLE_COLUMN_DATE);
                        Date dt = dateFormat.parse(date);
                        if (textNote != null) {
                            ContentValues newValues = new ContentValues();
                            newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, textNote);
                            newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, dt.getTime());
                            db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
                        } else {
                            return false;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean exportDB(File file, String type, final String[] data) {
        String result = "";
        Note note;
        Cursor cursor = getCursorAllNotes();
        JSONArray resultJson = new JSONArray();
        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance();
        while (cursor.moveToNext()) {
            note = new Note(cursor);
            if (type == data[0]) {
                JSONObject obj;
                obj = new JSONObject();
                try {
                    obj.put(NOTES_TABLE_COLUMN_TEXT_NOTE, note.getText_note());
                    obj.put(NOTES_TABLE_COLUMN_DATE, dateFormat.format(note.getDate()));
                    resultJson.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (type == data[1]) {
                result += note.getDate();
                result += ": ";
                result += note.getText_note();
                result += "\n";
            }
        }
        if (type == data[0]) {
            result = resultJson.toString();
        }
        if (file.canWrite()) {
            try {
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.println(result);
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
