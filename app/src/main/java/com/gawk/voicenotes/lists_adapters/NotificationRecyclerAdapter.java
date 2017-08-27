package com.gawk.voicenotes.lists_adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GAWK on 29.03.2017.
 */

public class NotificationRecyclerAdapter extends CursorRecyclerViewAdapter<NotificationRecyclerAdapter.ViewHolder> {

    private ActionsListNotes actionsListNotes;
    private SQLiteDBHelper db;

    public NotificationRecyclerAdapter(Context context, Cursor cursor, ActionsListNotes actionsListNotes, SQLiteDBHelper db) {
        super(context, cursor);
        this.actionsListNotes = actionsListNotes;
        this.db = db;
        this.db.connection();
    }

    public NotificationRecyclerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageViewSound, imageViewShake;
        public CheckBox checkBoxNotification;
        public TextView textViewTextNote, textViewDateNotification;
        public View parent;

        public ViewHolder(View v) {
            super(v);
            parent = v;
            checkBoxNotification =  v.findViewById(R.id.checkBoxNotification);
            imageViewSound = v.findViewById(R.id.imageViewSound);
            imageViewShake = v.findViewById(R.id.imageViewShake);
            textViewTextNote = v.findViewById(R.id.textViewTextNote);
            textViewDateNotification = v.findViewById(R.id.textViewDateNotification);
        }

        public void setData(final Cursor c, final NotificationRecyclerAdapter notificationRecyclerAdapter, SQLiteDBHelper db) {
            final int position = c.getPosition();
            Notification notification = new Notification(c);
            checkBoxNotification.setChecked(false);
            checkBoxNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    long id = notificationRecyclerAdapter.getItemId(position);
                    notificationRecyclerAdapter.getActionsListNotes().selectNotification(id);
                }
            });

            // Находим заметку для текущего оповещения
            long note_id = notification.getId_note();
            Cursor noteCursor = db.getNoteById(note_id);
            Note note = new Note(noteCursor);

            Calendar cToday = Calendar.getInstance();   // получаем сегодняшний день и время
            cToday.set(
                    cToday.get(Calendar.YEAR),
                    cToday.get(Calendar.MONTH),
                    cToday.get(Calendar.DAY_OF_MONTH),
                    23,59);   // меняем дату на начало дня

            // Выводим дату и время срабатывания оповещения
            Date date = notification.getDate();
            DateFormat dateFormat;
            if (date.before(cToday.getTime())) {
                dateFormat = SimpleDateFormat.getTimeInstance();
            } else {
                dateFormat = SimpleDateFormat.getDateTimeInstance();
            }
            textViewDateNotification.setText(dateFormat.format(date));

            // Выводим текст заметки, к которой относится оповещение
            textViewTextNote.setText(note.getText_note() + ". Shake = " + notification.isRepeat());

            // Задаем иконку для состояния звука оповещения
            if (notification.isSound()) {
                imageViewSound.setVisibility(View.VISIBLE);
            } else {
                imageViewSound.setVisibility(View.GONE);
            }

            // Задаем иконку для состояния вибрации оповещения
            if (notification.isShake()) {
                imageViewShake.setVisibility(View.VISIBLE);
            } else {
                imageViewShake.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public NotificationRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getmContext()).inflate(R.layout.item_notification_list, parent, false);
        return new NotificationRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationRecyclerAdapter.ViewHolder viewHolder, Cursor cursor) {
        NotificationRecyclerAdapter.ViewHolder holder = viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor, this, db);
    }

    @Override
    public int getItemCount() {
        if (this.db.isConnect()) return super.getItemCount();
        return 0;
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