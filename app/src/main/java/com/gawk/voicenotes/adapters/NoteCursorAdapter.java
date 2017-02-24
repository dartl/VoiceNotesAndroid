package com.gawk.voicenotes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by GAWK on 24.02.2017.
 */

public class NoteCursorAdapter extends CursorAdapter {
    private ActionsListNotes actionsListNotes;

    public NoteCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public NoteCursorAdapter(Context context, Cursor c, boolean autoRequery, ActionsListNotes actionsListNotes) {
        super(context, c, autoRequery);
        this.actionsListNotes = actionsListNotes;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_notes_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        // события с элементами
        ImageButton deleteIcon = (ImageButton) view.findViewById(R.id.buttonDeleteNote);
        final int position = cursor.getPosition();
        CheckBox checkBoxSelectNote = (CheckBox) view.findViewById(R.id.checkBoxSelectNote);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = getItemId(position);
                actionsListNotes.deleteNote(id);
            }
        });

        checkBoxSelectNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                long id = getItemId(position);
                actionsListNotes.selectNote(id,isChecked);
            }
        });

        // вывод даты и текста
        String text = cursor.getString(cursor.getColumnIndex
                (SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE));
        long datelong = cursor.getLong(cursor.getColumnIndex
                (SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE));

        TextView textView = (TextView) view.findViewById(R.id.textViewListText);
        TextView dateView = (TextView) view.findViewById(R.id.textViewListDate);

        textView.setText(text);

        DateFormat dateFormat;
        Date date = new Date(datelong);
        Calendar cToday = Calendar.getInstance();
        cToday.set(cToday.get(Calendar.YEAR), cToday.get(Calendar.MONTH), cToday.get(Calendar.DAY_OF_MONTH),0,0);
        if (date.after(cToday.getTime())) {
            dateFormat = SimpleDateFormat.getTimeInstance();
            dateView.setText(dateFormat.format(date));
        } else {
            dateFormat = SimpleDateFormat.getDateInstance();
            String date_and_time = dateFormat.format(date);
            dateFormat = SimpleDateFormat.getTimeInstance();
            date_and_time += dateFormat.format(date);
            dateView.setText(date_and_time);
        }


    }
}
