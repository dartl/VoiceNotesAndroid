package com.gawk.voicenotes.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.NewNote;
import com.gawk.voicenotes.NoteView;
import com.gawk.voicenotes.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Адаптер для указателя на список заметок, чтобы стилизовать их и задать события
 * @author GAWK
 */

public class NoteCursorAdapter extends CursorAdapter {
    /**
     * Ссылка на интерфейс, определяющий методы, работающие с фрагментами для адаптеров {@value}
     */
    private ActionsListNotes actionsListNotes;

    /**
     * Стандартный конструкторв
     * @param context контекст, к которому привязан адаптер
     * @param c указатель на список заметок из БД
     * @param autoRequery неизвестная переменная
     */
    public NoteCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    /**
     * Стандартный конструкторв
     * @param context контекст, к которому привязан адаптер
     * @param c указатель на список заметок из БД
     * @param autoRequery неизвестная переменная
     * @param actionsListNotes интерфейс, который определит методы для доступа к фрагментам
     */
    public NoteCursorAdapter(Context context, Cursor c, boolean autoRequery, ActionsListNotes actionsListNotes) {
        super(context, c, autoRequery);
        this.actionsListNotes = actionsListNotes;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_notes_list, parent, false);
    }

    /**
     * Привязывает элементы из указателя к элементам представления
     * @param view ссылка на представление
     * @param context ссылка на контекст
     * @param cursor указатель на список заметок из БД
     */
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final int position = cursor.getPosition();

        // Находим элементы в представлении
        ImageButton deleteIcon = (ImageButton) view.findViewById(R.id.buttonDeleteNote);
        CheckBox checkBoxSelectNote = (CheckBox) view.findViewById(R.id.checkBoxSelectNote);
        TextView textView = (TextView) view.findViewById(R.id.textViewListText);
        TextView dateView = (TextView) view.findViewById(R.id.textViewListDate);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = getItemId(position);
                actionsListNotes.showDialogDelete(id,0);
            }
        });

        checkBoxSelectNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                long id = getItemId(position);
                actionsListNotes.selectNote(id,isChecked);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                long id = getItemId(position);
                intent = new Intent(v.getContext(), NoteView.class);
                intent.putExtra("id",id);
                v.getContext().startActivity(intent);
            }
        });

        // Вывод текста заметки
        String text = cursor.getString(cursor.getColumnIndex
                (SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE));
        textView.setText(text);

        // Вывод даты заметки
        long datelong = cursor.getLong(cursor.getColumnIndex
                (SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE));  // получаем дату в виде числа
        DateFormat dateFormat;
        Date date = new Date(datelong);
        Calendar cToday = Calendar.getInstance();   // получаем сегодняшний день и время
        cToday.set(
                cToday.get(Calendar.YEAR),
                cToday.get(Calendar.MONTH),
                cToday.get(Calendar.DAY_OF_MONTH),
                0,0);   // меняем дату на начало дня
        if (date.after(cToday.getTime())) { // если дата сегодняшняя, то выводим только время
            dateFormat = SimpleDateFormat.getTimeInstance();
            dateView.setText(dateFormat.format(date));
        } else {    // если дата не сегодняшняя, то выводим с датой
            dateFormat = SimpleDateFormat.getDateTimeInstance();
            String date_and_time = dateFormat.format(date);
            dateView.setText(date_and_time);
        }
    }
}
