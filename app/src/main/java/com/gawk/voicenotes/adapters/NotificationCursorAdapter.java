package com.gawk.voicenotes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ActionsListNotes actionsListNotification;

    public NotificationCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public NotificationCursorAdapter(Context context, Cursor c, boolean autoRequery, ActionsListNotes actionsListNotification) {
        super(context, c, autoRequery);
        this.actionsListNotification = actionsListNotification;
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

        ImageView imageViewSound = (ImageView) view.findViewById(R.id.imageViewSound);
        ImageView imageViewShake = (ImageView) view.findViewById(R.id.imageViewShake);
        int sound = cursor.getInt(cursor.getColumnIndex
                (SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_SOUND));
        int shake = cursor.getInt(cursor.getColumnIndex
                (SQLiteDBHelper.NOTIFICATIONS_TABLE_COLUMN_VIBRATE));
        if (sound == 0) {
            imageViewSound.setImageResource(R.drawable.ic_volume_off_black_18dp);
        } else {
            imageViewSound.setImageResource(R.drawable.ic_volume_up_black_18dp);
        }

        if (shake == 0) {
            imageViewShake.setImageResource(R.drawable.ic_vibration_off_black_18dp);
        } else {
            imageViewShake.setImageResource(R.drawable.ic_vibration_black_18dp);
        }

    }
}
