package com.gawk.voicenotes.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.NoteView;
import com.gawk.voicenotes.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GAWK on 24.03.2017.
 */

public class NoteRecyclerAdapter extends CursorRecyclerViewAdapter<NoteRecyclerAdapter.ViewHolder> implements View.OnLongClickListener {

    private ActionsListNotes actionsListNotes;

    public NoteRecyclerAdapter(Context context, Cursor cursor, ActionsListNotes actionsListNotes) {
        super(context, cursor);
        this.actionsListNotes = actionsListNotes;
    }

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, dateView;
        public View parent;

        public ViewHolder(View v) {
            super(v);
            parent = v;
            textView = v.findViewById(R.id.textViewListText);
            dateView = v.findViewById(R.id.textViewListDate);
        }

        public void setData(final Cursor c, final NoteRecyclerAdapter noteRecyclerAdapter) {
            final int position = c.getPosition();
            final long id = noteRecyclerAdapter.getItemId(getLayoutPosition());

            changeItemSelect(noteRecyclerAdapter.getActionsListNotes().checkSelectNote(id));
            textView.setText(c.getString(c.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE)));

            parent.setLongClickable(true);
            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.e("GAWK_ERR","LONG CLICK - " + id);
                    changeItemSelect(noteRecyclerAdapter.getActionsListNotes().selectNote(id));
                    return true;
                }
            });

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    long id = noteRecyclerAdapter.getItemId(position);
                    intent = new Intent(v.getContext(), NoteView.class);
                    intent.putExtra("id",id);
                    v.getContext().startActivity(intent);
                }
            });

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

        private void changeItemSelect(boolean state) {
            if (state) {
                parent.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorPrimary));
                textView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textColorPrimary));
                dateView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.textColorPrimary));
            } else {
                parent.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorTransparent));
                textView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorGrey900));
                dateView.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorGrey700));
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
        cursor.moveToPosition(cursor.getPosition());
        viewHolder.setData(cursor, this);
    }

    @Override
    public boolean onLongClick(View view) {
        view.setBackgroundColor(ContextCompat.getColor(getmContext(), R.color.colorPrimary));
        return false;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public ActionsListNotes getActionsListNotes() {
        return actionsListNotes;
    }

    public void setActionsListNotes(ActionsListNotes actionsListNotes) {
        this.actionsListNotes = actionsListNotes;
    }
}
