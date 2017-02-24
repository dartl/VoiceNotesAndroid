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
import com.gawk.voicenotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GAWK on 24.02.2017.
 */

public class NotificationCursorAdapter extends CursorAdapter {
    private ActionsListNotes actionsListNotes;

    public NotificationCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public NotificationCursorAdapter(Context context, Cursor c, boolean autoRequery, ActionsListNotes actionsListNotes) {
        super(context, c, autoRequery);
        this.actionsListNotes = actionsListNotes;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_notification_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        long note_id = cursor.getLong(cursor.getColumnIndex
                (SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_ID_NOTE));
        SQLiteDBHelper db = SQLiteDBHelper.getInstance(context);
        Cursor noteCursor = db.getNoteById(note_id);
        Note note = new Note(noteCursor);

        long datelong = cursor.getLong(cursor.getColumnIndex
                (SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_DATE));

        DateFormat dateFormat;
        Date date = new Date(datelong);
        dateFormat = SimpleDateFormat.getDateTimeInstance();

        TextView textViewDateNotification = (TextView) view.findViewById(R.id.textViewDateNotification);
        textViewDateNotification.setText(dateFormat.format(date));

        TextView textViewTextNote = (TextView) view.findViewById(R.id.textViewTextNote);
        textViewTextNote.setText(note.getText_note());
    }
}
