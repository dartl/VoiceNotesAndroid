package com.gawk.voicenotes.lists_adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GAWK on 29.03.2017.
 */

public class NotificationRecyclerAdapter extends CursorRecyclerViewAdapter<NotificationRecyclerAdapter.ViewHolder> {

    private ActionsListNotes actionsListNotes;
    private SQLiteDBHelper db;
    private boolean mViewNote = false;

    public NotificationRecyclerAdapter(Context context, Cursor cursor, ActionsListNotes actionsListNotes, SQLiteDBHelper db) {
        super(context, cursor);
        this.actionsListNotes = actionsListNotes;
        this.db = db;
        this.db.connection();
    }

    public NotificationRecyclerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewSound, imageViewShake;
        public TextView textViewTextNote, textViewDateNotification;
        private ImageButton mImageButtonNotificationIcon, mImageButtonMoreMenu;
        CardView cardView;
        public View parent;

        public ViewHolder(View v) {
            super(v);
            parent = v;
            imageViewSound = v.findViewById(R.id.imageViewSound);
            imageViewShake = v.findViewById(R.id.imageViewShake);
            textViewTextNote = v.findViewById(R.id.textViewTextNote);
            textViewDateNotification = v.findViewById(R.id.textViewDateNotification);
            mImageButtonNotificationIcon = v.findViewById(R.id.imageButtonNotificationIcon);
            mImageButtonMoreMenu = v.findViewById(R.id.imageButtonMoreMenu);
            cardView = v.findViewById(R.id.card_view);
        }

        public void setData(final Cursor c, final NotificationRecyclerAdapter notificationRecyclerAdapter, SQLiteDBHelper db) {
            final long id = notificationRecyclerAdapter.getItemId(getLayoutPosition());

            Notification notification = new Notification(c);
            boolean stateSelected = notificationRecyclerAdapter.getActionsListNotes().checkSelectElement(id);

            changeItemSelect(stateSelected);

            mImageButtonNotificationIcon.setImageResource(R.drawable.ic_alarm_white_24dp);
            mImageButtonNotificationIcon.setColorFilter(ContextCompat.getColor(notificationRecyclerAdapter.getContext(), R.color.colorPrimary500));

            mImageButtonNotificationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(notificationRecyclerAdapter.getActionsListNotes().isStateSelected()) {
                        parent.performLongClick();
                    }
                }
            });

            if (notificationRecyclerAdapter.getActionsListNotes().isStateSelected()) {
                if (stateSelected) {
                    mImageButtonNotificationIcon.setImageResource(R.drawable.ic_done_white_24dp);
                    mImageButtonNotificationIcon.setColorFilter(ContextCompat.getColor(mImageButtonNotificationIcon.getContext(), R.color.colorWhite));
                    mImageButtonNotificationIcon.setBackgroundResource(R.drawable.list_item_circle_primary);
                } else {
                    mImageButtonNotificationIcon.setImageResource(R.drawable.ic_alarm_white_24dp);
                    mImageButtonNotificationIcon.setBackgroundResource(R.drawable.list_item_circle);
                }
                mImageButtonMoreMenu.setVisibility(View.INVISIBLE);
            } else {
                mImageButtonNotificationIcon.setBackgroundResource(0);
                mImageButtonMoreMenu.setVisibility(View.VISIBLE);
            }

            mImageButtonMoreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notificationRecyclerAdapter.getActionsListNotes().showBottomMenu(id);
                }
            });

            parent.setLongClickable(true);
            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    changeItemSelect(notificationRecyclerAdapter.getActionsListNotes().selectElement(id));
                    return true;
                }
            });

            // Находим заметку для текущего оповещения
            long note_id = notification.getId_note();
            Cursor noteCursor = db.getNoteById(note_id);
            Log.e("GAWK_ERR","SetData() notification = " + notification.toString());

            noteCursor.moveToFirst();

            if (noteCursor.getCount() == 0) {
                db.deleteAllNotificationByNote(note_id);
                return;
            }

            if (!notificationRecyclerAdapter.isViewNote()) {
                Note note = new Note(noteCursor);
                // Выводим текст заметки, к которой относится оповещение
                textViewTextNote.setText(note.getText_note());
            } else {
                textViewTextNote.setVisibility(View.GONE);
                parent.setLongClickable(false);
            }

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

            // Задаем иконку для состояния звука оповещения
            if (notification.isSound()) {
                imageViewSound.setColorFilter(ContextCompat.getColor(notificationRecyclerAdapter.getContext(), R.color.colorPrimary500));
            } else {
                imageViewSound.setColorFilter(ContextCompat.getColor(notificationRecyclerAdapter.getContext(), R.color.colorGrey300));
            }

            // Задаем иконку для состояния вибрации оповещения
            if (notification.isShake()) {
                imageViewShake.setColorFilter(ContextCompat.getColor(notificationRecyclerAdapter.getContext(), R.color.colorPrimary500));
            } else {
                imageViewShake.setColorFilter(ContextCompat.getColor(notificationRecyclerAdapter.getContext(), R.color.colorGrey300));
            }
        }

        private void changeItemSelect(boolean state) {
            if (state) {
                cardView.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorSelectListItem));
            } else {
                cardView.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorWhite));
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public NotificationRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.item_notification_list, parent, false);
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
        if (db.isConnect()) return super.getItemCount();
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

    public boolean isViewNote() {
        return mViewNote;
    }

    public void setViewNote(boolean mViewNote) {
        this.mViewNote = mViewNote;
    }
}