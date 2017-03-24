package com.gawk.voicenotes.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GAWK on 24.03.2017.
 */

public class NoteRecyclerAdapter extends  CursorRecyclerViewAdapter<NoteRecyclerAdapter.ViewHolder> {

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageButton deleteIcon;
        public CheckBox checkBoxSelectNote;
        public TextView textView, dateView;

        public ViewHolder(View v) {
            super(v);
            deleteIcon = (ImageButton) v.findViewById(R.id.buttonDeleteNote);
            checkBoxSelectNote = (CheckBox) v.findViewById(R.id.checkBoxSelectNote);
            textView = (TextView) v.findViewById(R.id.textViewListText);
            dateView = (TextView) v.findViewById(R.id.textViewListDate);
        }

        public void setData(Cursor c) {
            textView.setText(c.getString(c.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE)));

            long dateLong = c.getLong(c.getColumnIndex
                    (SQLiteDBHelper.NOTES_TABLE_COLUMN_DATE));  // получаем дату в виде числа
            DateFormat dateFormat;
            Date date = new Date(dateLong);
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

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public NoteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getmContext()).inflate(R.layout.item_notes_list, parent, false);
        return new NoteRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteRecyclerAdapter.ViewHolder viewHolder, Cursor cursor) {
        NoteRecyclerAdapter.ViewHolder holder = viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
}
