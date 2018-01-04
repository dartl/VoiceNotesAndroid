package com.gawk.voicenotes.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gawk.voicenotes.models.Category;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;
import com.gawk.voicenotes.models.Statistics;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Адаптер для подключения к БД
 * @author GAWK
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static SQLiteDBHelper sInstance;
    private SQLiteDatabase db;
    private Statistics mStatistics;
    private NotificationAdapter mNotificationAdapter;

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "VOICE_NOTES.DB";

    /* Поля и переменные таблицы Заметки */
    public static final String NOTES_TABLE_NAME = "NOTES";
    public static final String NOTES_TABLE_COLUMN_ID = "_id";
    public static final String NOTES_TABLE_COLUMN_TEXT_NOTE = "TEXT_NOTE";
    public static final String NOTES_TABLE_COLUMN_DATE = "DATE";
    public static final String NOTES_TABLE_COLUMN_CATEGORY = "CATEGORY";

    /* Поля и переменные таблицы Напоминания */
    public static final String NOTIFICATIONS_TABLE_NAME = "NOTIFICATIONS";
    public static final String NOTIFICATIONS_TABLE_COLUMN_ID = "_id";
    public static final String NOTIFICATIONS_TABLE_COLUMN_ID_NOTE = "ID_NOTE";
    public static final String NOTIFICATIONS_TABLE_COLUMN_DATE = "DATE";
    public static final String NOTIFICATIONS_TABLE_COLUMN_SOUND = "SOUND";
    public static final String NOTIFICATIONS_TABLE_COLUMN_VIBRATE = "VIBRATE";
    public static final String NOTIFICATIONS_TABLE_COLUMN_REPEAT = "REPEAT";

    /* Поля и переменные таблицы Категории */
    public static final String CATEGORIES_TABLE_NAME = "CATEGORIES";
    public static final String CATEGORIES_TABLE_COLUMN_ID = "_id";
    public static final String CATEGORIES_TABLE_COLUMN_NAME = "NAME_CATEGORY";

    /**
     * Метод для получения ссылки на статический класс
     */
    public static synchronized SQLiteDBHelper getInstance(Context context) {
        if (sInstance == null) {
            if (context == null) return null;
            sInstance = new SQLiteDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Стандартный конструктор для получения экземпляра класса
     */
    public SQLiteDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mNotificationAdapter = new NotificationAdapter(context);
        mStatistics = new Statistics(context);
    }

    /**
     * Создание БД, если её ещё нет
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(
                "create table " + NOTES_TABLE_NAME +
                        "(" + NOTES_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTES_TABLE_COLUMN_TEXT_NOTE + " text, " + NOTES_TABLE_COLUMN_DATE + " integer, " +NOTES_TABLE_COLUMN_CATEGORY+ " integer)"
        );

        db.execSQL(
                "create table " + NOTIFICATIONS_TABLE_NAME +
                        "(" + NOTIFICATIONS_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTIFICATIONS_TABLE_COLUMN_ID_NOTE + " integer, " + NOTIFICATIONS_TABLE_COLUMN_DATE + " text, "
                        + NOTIFICATIONS_TABLE_COLUMN_SOUND + " integer, " + NOTIFICATIONS_TABLE_COLUMN_REPEAT + " integer, " + NOTIFICATIONS_TABLE_COLUMN_VIBRATE + " integer)"
        );

        db.execSQL(
                "create table " + CATEGORIES_TABLE_NAME +
                        "(" + CATEGORIES_TABLE_COLUMN_ID + " integer primary key, " + CATEGORIES_TABLE_COLUMN_NAME + " text)"
        );
    }

    /**
     * Обновление БД, если версия другая
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion <= 1) {
            upgradeTo2(db);
        }
        if (oldVersion <= 2) {
            upgradeTo3(db);
        }
        if (oldVersion <= 3) {
            upgradeTo4(db);
        }
    }

    private void upgradeTo2(SQLiteDatabase db) {
        // Создаем таблицу оповещений
        db.execSQL(
                "create table " + NOTIFICATIONS_TABLE_NAME +
                        "(" + NOTIFICATIONS_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTIFICATIONS_TABLE_COLUMN_ID_NOTE + " integer, " + NOTIFICATIONS_TABLE_COLUMN_DATE + " text, "
                        + NOTIFICATIONS_TABLE_COLUMN_SOUND + " integer, " + NOTIFICATIONS_TABLE_COLUMN_VIBRATE + " integer)"
        );
        Cursor cursor = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE
                + " DESC", null);

        ArrayList<Note> array = new ArrayList<Note>();

        Note temp;
        while (cursor.moveToNext()) {
            String data = cursor.getString(cursor.getColumnIndex(NOTES_TABLE_COLUMN_DATE));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");
            try {
                Date date = dateFormat.parse(data);
                temp = new Note(cursor.getLong(cursor.getColumnIndex(NOTES_TABLE_COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(NOTES_TABLE_COLUMN_TEXT_NOTE)),date, -1);
                array.add(temp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        db.execSQL("DROP TABLE IF EXISTS "+NOTES_TABLE_NAME);
        db.execSQL(
                "create table " + NOTES_TABLE_NAME +
                        "(" + NOTES_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTES_TABLE_COLUMN_TEXT_NOTE + " text, " + NOTES_TABLE_COLUMN_DATE + " integer)"
        );

        Iterator<Note> crunchifyIterator = array.iterator();
        while (crunchifyIterator.hasNext()) {
            temp = crunchifyIterator.next();
            ContentValues newValues = new ContentValues();
            newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, temp.getText_note());
            newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, temp.getDate().getTime());
            long i = db.insert(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
        }
        // Обновляем таблицу заметок
    }

    private void upgradeTo3(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE
                + " DESC", null);
        ArrayList<Notification> array = new ArrayList<>();
        Notification temp;
        while (cursor.moveToNext()) {
            temp = new Notification(cursor.getLong(cursor.getColumnIndex(NOTIFICATIONS_TABLE_COLUMN_ID)),
                    cursor.getLong(cursor.getColumnIndex(NOTIFICATIONS_TABLE_COLUMN_ID_NOTE)),
                    new Date(cursor.getLong(cursor.getColumnIndex(NOTIFICATIONS_TABLE_COLUMN_DATE))),
                    cursor.getInt(cursor.getColumnIndex(NOTIFICATIONS_TABLE_COLUMN_SOUND)),
                    cursor.getInt(cursor.getColumnIndex(NOTIFICATIONS_TABLE_COLUMN_VIBRATE)),
                    0
            );
            array.add(temp);
        }
        db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS_TABLE_NAME);
        // Создаем таблицу оповещений
        db.execSQL(
                "create table " + NOTIFICATIONS_TABLE_NAME +
                        "(" + NOTIFICATIONS_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTIFICATIONS_TABLE_COLUMN_ID_NOTE + " integer, " + NOTIFICATIONS_TABLE_COLUMN_DATE + " text, "
                        + NOTIFICATIONS_TABLE_COLUMN_SOUND + " integer, " + NOTIFICATIONS_TABLE_COLUMN_REPEAT + " integer, " + NOTIFICATIONS_TABLE_COLUMN_VIBRATE + " integer)"
        );

        for (Notification anArray : array) {
            ContentValues newValues = new ContentValues();
            newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE, anArray.getId());
            newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE, anArray.getId_note());
            newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE, anArray.getDate().getTime());
            newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_SOUND, anArray.isSound());
            newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_REPEAT, anArray.isRepeat());
            newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_VIBRATE, anArray.isShake());
            long i = db.insert(SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME, null, newValues);
        }
        // Обновляем таблицу заметок
    }

    private void upgradeTo4(SQLiteDatabase db) {
        setConnection(db);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE_NAME);
        // Создаем таблицу оповещений
        db.execSQL(
                "create table " + CATEGORIES_TABLE_NAME +
                        "(" + CATEGORIES_TABLE_COLUMN_ID + " integer primary key, " + CATEGORIES_TABLE_COLUMN_NAME + " text)"
        );

        Cursor cursor = getCursorAllNotes();

        ArrayList<Note> array = new ArrayList<Note>();

        if (cursor.moveToFirst()) {
            do {
                Date date = new Date(cursor.getLong(cursor.getColumnIndex(NOTES_TABLE_COLUMN_DATE)));
                array.add(new Note(cursor.getLong(cursor.getColumnIndex(NOTES_TABLE_COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(NOTES_TABLE_COLUMN_TEXT_NOTE)),
                        date));
            } while (cursor.moveToNext());
        }

        db.execSQL("DROP TABLE IF EXISTS "+NOTES_TABLE_NAME);

        db.execSQL(
                "create table " + NOTES_TABLE_NAME +
                        "(" + NOTES_TABLE_COLUMN_ID + " integer primary key, " +
                        NOTES_TABLE_COLUMN_TEXT_NOTE + " text, " + NOTES_TABLE_COLUMN_DATE + " integer, " +NOTES_TABLE_COLUMN_CATEGORY+ " integer)"
        );

        for (Note anArray : array) {
            long i = saveNote(anArray);
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

    public void setConnection(SQLiteDatabase db) {
        this.db = db;
    }

    /*
        Методы для работы с Категориями
     */
    public boolean saveCategory(Category category) {
        if (!isConnect()) {
            connection();
        }
        mStatistics.addPointCreateNotifications();
        ContentValues newValues = new ContentValues();
        newValues.put(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_NAME, category.getName());
        if (category.getId() != -1) {
            newValues.put(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_ID, category.getId());
        }
        return db.replace(SQLiteDBHelper.CATEGORIES_TABLE_NAME, null, newValues) >= -1;
    }

    public String getNameCategory(long id) {
        if (!isConnect()) {
            connection();
        }
        Cursor category = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.CATEGORIES_TABLE_NAME + " WHERE " + SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_ID
                + " = " + id, null);
        if (category.getCount() == 0) {
            return "";
        }
        category.moveToFirst();
        return category.getString(category.getColumnIndex(SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_NAME));
    }

    public boolean removeCategory(long id) {
        if (!isConnect()) {
            connection();
        }
        int deleteRow = db.delete(SQLiteDBHelper.CATEGORIES_TABLE_NAME, "_id = ?" ,new String[] { String.valueOf(id) });
        ContentValues cv = new ContentValues();
        cv.put(NOTES_TABLE_COLUMN_CATEGORY,-1);
        db.update(NOTES_TABLE_NAME, cv, NOTES_TABLE_COLUMN_CATEGORY + " = " + id, null);
        return deleteRow == 1;
    }

    public Cursor getCursorAllCategories() {
        if (!isConnect()) {
            connection();
        }
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.CATEGORIES_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.CATEGORIES_TABLE_COLUMN_ID
                + " DESC", null);
    }

    /*
        Методы для работы с Notification
     */

    /**
     * Получаем указатель на список всех оповещений
     * @return возвращает результат запроса к БД
     */
    public Cursor getCursorAllNotification() {
        if (!isConnect()) {
            connection();
        }
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE
                + " ASC", null);
    }

    /**
     * Получаем указатель на напоминание по ид
     * @return возвращает результат запроса к БД
     */
    public Cursor getCursorNotification(long id) {
        if (!isConnect()) {
            connection();
        }
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME + " WHERE " + SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID
                + " = " + id + " ORDER BY " + SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE
                 + " ASC", null);
    }

    /**
     * Удаляет из БД все старые заметки
     * @return возвращает результат запроса к БД
     */
    public boolean deleteAllOldNotification() {
        if (!isConnect()) {
            connection();
        }
        Cursor oldNotificationCursor = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME + " WHERE " + SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE
                + " < " + new Date().getTime(), null);
        while (oldNotificationCursor.moveToNext()) {
            deleteNotification(oldNotificationCursor.getInt(oldNotificationCursor.getColumnIndex(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID)));
        }
        return true;
    }

    /**
     * Получаем указатель на список всех оповещений, привязанных к заметке
     * @param id идентификатор заметки
     * @return возвращает результат запроса к БД
     */
    public Cursor getAllNotificationByNote(long id) {
        if (!isConnect()) {
            connection();
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
     * @return возвращает результат запроса к БД
     */
    public long saveNotification(Notification notification) {
        if (!isConnect()) {
            connection();
        }
        mStatistics.addPointCreateNotifications();
        ContentValues newValues = new ContentValues();
        if (notification.getId() != -1) {
            newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID, notification.getId());
        }
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE, notification.getId_note());
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE, notification.getDate().getTime());
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_SOUND, notification.isSound());
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_VIBRATE, notification.isShake());
        newValues.put(SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_REPEAT, notification.isRepeat());
        return db.replace(SQLiteDBHelper.NOTIFICATIONS_TABLE_NAME, null, newValues);
    }

    /**
     * Сохранить новую заметку или обновить существующую
     * <p><b>action = 0</b> - добавить новую; <b>action = 1</b> - обновить существующую</p>
     * @param id идентификатор удаляемого оповещения
     * @return результат удаления
     */
    public boolean deleteNotification(long id) {
        if(!isConnect()) {
            connection();
        }
        mNotificationAdapter.removeNotify(id);
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
        if (!isConnect()) {
            connection();
        }
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " ORDER BY " + SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE
                + " DESC", null);
    }

    // Получить указатель на все заметки с искомым вхождением
    public Cursor getCursorAllNotes(String text, long category) {
        if (!isConnect()) {
            connection();
        }
        String where ="";
        if (text != null || category != -1) where = "WHERE ";
        if (text != null) {
            where += "lower(" +
                    SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE + ") like lower('%" + text
                            + "%')";
            if (category != -1) {
                where += " AND ";
            }
        }
        if (category != -1) {
            where += SQLiteDBHelper.NOTES_TABLE_COLUMN_CATEGORY + " = " + category;
        }
        return db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " " + where + " ORDER BY " + SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE
                + " DESC", null);
    }

    public Cursor getNoteById(long id){
        if (!isConnect()) {
            connection();
        }
        Cursor c = db.rawQuery("SELECT * FROM " +
                SQLiteDBHelper.NOTES_TABLE_NAME + " WHERE "+NOTES_TABLE_COLUMN_ID+" = " + id, null);
        c.moveToFirst();
        return c;
    }

    // Сохранить новую заметку или обновить существующую
    // action = 0 - добавить новую; action = 1 - обновить существующую
    public long saveNote(Note note) {
        if (!isConnect()) {
            connection();
        }
        if (note == null) return -1;
        ContentValues newValues = new ContentValues();
        if (note.getId() != -1) {
            newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_ID, note.getId());
        }
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE, note.getText_note());
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE, note.getDate().getTime());
        newValues.put(SQLiteDBHelper.NOTES_TABLE_COLUMN_CATEGORY, note.getCategoryId());
        return db.replace(SQLiteDBHelper.NOTES_TABLE_NAME, null, newValues);
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
            mStatistics.addPointRemoveNotes();
            return true;
        }
        return false;
    }

    public long getCountNotes() {
        if (!db.isOpen()) {
            return -1;
        }
        return DatabaseUtils.queryNumEntries(db, NOTES_TABLE_NAME);
    }

    public boolean importDB(File file) {
        String json = "";
        if (file.canRead()) {
            try {
                FileInputStream fin = new FileInputStream(file);
                json = convertStreamToString(fin);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject jReader = new JSONObject(json);

                JSONArray categories = jReader.getJSONArray(CATEGORIES_TABLE_NAME);
                if (categories.length() >= 1) {
                    JSONObject objNote;
                    for (int  i = 0; i < categories.length(); i++) {
                        objNote=(JSONObject)categories.get(i);
                        Category category = new Category(objNote);
                        saveCategory(category);
                    }
                }

                JSONArray notes = jReader.getJSONArray(NOTES_TABLE_NAME);
                if (notes.length() >= 1) {
                    JSONObject objNote;
                    for (int  i = 0; i < notes.length(); i++) {
                        objNote=(JSONObject)notes.get(i);
                        Note note = new Note(objNote);
                        saveNote(note);
                    }
                }

                mStatistics.addPointImports();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean exportDB(File file, String type, final String[] data) {
        StringBuilder result = new StringBuilder();
        Note note;
        Cursor cursor = getCursorAllNotes();

        if (type.equals(data[0])) {
            JSONObject resultObject = new JSONObject();

            JSONArray notesArray = new JSONArray();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                note = new Note(cursor);
                notesArray.put(note.toJSON());
            }

            Cursor cursorAllCategories = getCursorAllCategories();
            JSONArray categoryArray = new JSONArray();
            for (int i = 0; i < cursorAllCategories.getCount(); i++) {
                cursorAllCategories.moveToPosition(i);
                Category category = new Category(cursorAllCategories);
                categoryArray.put(category.toJSON());
            }

            try {
                resultObject.put(NOTES_TABLE_NAME,notesArray);
                resultObject.put(CATEGORIES_TABLE_NAME,categoryArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            result = new StringBuilder(resultObject.toString());
        } else if (type.equals(data[1])) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                note = new Note(cursor);
                result.append(note.getDate());
                result.append(": ");
                result.append(note.getText_note());
                result.append("\n");
            }
        }

        if (file.canWrite()) {
            try {
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.println(result);
                printWriter.close();
                mStatistics.addPointExports();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public boolean isConnect() {
        return db != null && db.isOpen();
    }

}
