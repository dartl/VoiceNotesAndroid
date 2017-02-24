package com.gawk.voicenotes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public NoteCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_notes_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
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
