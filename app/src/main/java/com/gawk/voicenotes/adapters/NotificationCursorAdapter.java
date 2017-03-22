package com.gawk.voicenotes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Адаптер для указателя на список оповещений, чтобы стилизовать их и задать события
 * @author GAWK
 */

public class NotificationCursorAdapter extends CursorAdapter {
    /**
     * Ссылка на интерфейс, определяющий методы, работающие с фрагментами для адаптеров {@value}
     */
    private ActionsListNotes actionsListNotification;

    /**
     * Стандартный конструкторв
     * @param context контекст, к которому привязан адаптер
     * @param c указатель на список заметок из БД
     * @param autoRequery неизвестная переменная
     */
    public NotificationCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    /**
     * Стандартный конструкторв
     * @param context контекст, к которому привязан адаптер
     * @param c указатель на список заметок из БД
     * @param autoRequery неизвестная переменная
     * @param actionsListNotification интерфейс, который определит методы для доступа к фрагментам
     */
    public NotificationCursorAdapter(Context context, Cursor c, boolean autoRequery, ActionsListNotes actionsListNotification) {
        super(context, c, autoRequery);
        this.actionsListNotification = actionsListNotification;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_notification_list, parent, false);
    }

    /**
     * Привязывает элементы из указателя к элементам представления
     * @param view ссылка на представление
     * @param context ссылка на контекст
     * @param cursor указатель на список заметок из БД
     */
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        // Определяем необходимые переменные
        long note_id, datelong;
        final int position = cursor.getPosition();

        // Подключаемся к БД
        SQLiteDBHelper db = SQLiteDBHelper.getInstance(context);
        db.connection();
        // Находим элементы в представлении
        TextView textViewTextNote = (TextView) view.findViewById(R.id.textViewTextNote);
        TextView textViewDateNotification = (TextView) view.findViewById(R.id.textViewDateNotification);
        ImageView imageViewSound = (ImageView) view.findViewById(R.id.imageViewSound);
        ImageView imageViewShake = (ImageView) view.findViewById(R.id.imageViewShake);
        CheckBox checkBoxNotification = (CheckBox) view.findViewById(R.id.checkBoxNotification);

        checkBoxNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                long id = getItemId(position);
                actionsListNotification.selectNote(id,isChecked);
            }
        });

        // Находим заметку для текущего оповещения
        note_id = cursor.getLong(cursor.getColumnIndex
                (SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE));
        Cursor noteCursor = db.getNoteById(note_id);
        Note note = new Note(noteCursor);

        // Выводим дату и время срабатывания оповещения
        datelong = cursor.getLong(cursor.getColumnIndex
                (SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE));
        DateFormat dateFormat;
        Date date = new Date(datelong);
        dateFormat = SimpleDateFormat.getDateTimeInstance();
        textViewDateNotification.setText(dateFormat.format(date));

        // Выводим текст заметки, к которой относится оповещение
        textViewTextNote.setText(note.getText_note());

        // Задаем иконку для состояния звука оповещения
        int sound = cursor.getInt(cursor.getColumnIndex
                (SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_SOUND));
        if (sound == 0) {
            imageViewSound.setImageResource(R.drawable.ic_volume_off_black_18dp);
        } else {
            imageViewSound.setImageResource(R.drawable.ic_volume_up_black_18dp);
        }

        // Задаем иконку для состояния вибрации оповещения
        int shake = cursor.getInt(cursor.getColumnIndex
                (SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_VIBRATE));
        if (shake == 0) {
            imageViewShake.setImageResource(R.drawable.ic_vibration_off_black_18dp);
        } else {
            imageViewShake.setImageResource(R.drawable.ic_vibration_black_18dp);
        }
    }
}
