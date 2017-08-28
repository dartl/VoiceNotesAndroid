package com.gawk.voicenotes.lists_adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.NoteView;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GAWK on 24.03.2017.
 */

public class NoteRecyclerAdapter extends CursorRecyclerViewAdapter<NoteRecyclerAdapter.ViewHolder> implements View.OnLongClickListener {

    private ActionsListNotes actionsListNotes;
    private Boolean mStateSelected = false;
    private ArrayList mSelectNotes = new ArrayList<Long>();

    public NoteRecyclerAdapter(Context context, Cursor cursor, ActionsListNotes actionsListNotes) {
        super(context, cursor);
        this.actionsListNotes = actionsListNotes;
    }

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, dateView;
        ImageButton mImageButtonIconNote, mImageButtonMoreMenu;
        CardView cardView;
        View parent;
        public ViewHolder(View v) {
            super(v);
            parent = v;
            mImageButtonIconNote = v.findViewById(R.id.imageButtonIconNote);
            mImageButtonMoreMenu = v.findViewById(R.id.imageButtonMoreMenu);
            textView = v.findViewById(R.id.textViewListText);
            dateView = v.findViewById(R.id.textViewListDate);
            cardView = v.findViewById(R.id.card_view);
        }

        public void setData(final Cursor c, final NoteRecyclerAdapter noteRecyclerAdapter) {
            final int position = c.getPosition();
            final long id = noteRecyclerAdapter.getItemId(getLayoutPosition());

            changeItemSelect(noteRecyclerAdapter.getActionsListNotes().checkSelectElement(id));
            textView.setText(c.getString(c.getColumnIndex(SQLiteDBHelper.NOTES_TABLE_COLUMN_TEXT_NOTE)));

            mImageButtonIconNote.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
            mImageButtonIconNote.setColorFilter(ContextCompat.getColor(noteRecyclerAdapter.getContext(), R.color.colorPrimary500));

            if (noteRecyclerAdapter.isStateSelected()) {
                if (noteRecyclerAdapter.getSelectNotes().contains(id)) {
                    mImageButtonIconNote.setImageResource(R.drawable.ic_done_white_24dp);
                    mImageButtonIconNote.setColorFilter(ContextCompat.getColor(noteRecyclerAdapter.getContext(), R.color.colorWhite), PorterDuff.Mode.MULTIPLY);
                    mImageButtonIconNote.setBackgroundResource(R.drawable.list_item_circle_primary);
                } else {
                    mImageButtonIconNote.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
                    mImageButtonIconNote.setBackgroundResource(R.drawable.list_item_circle);
                }
                mImageButtonIconNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        parent.performLongClick();
                    }
                });
                mImageButtonMoreMenu.setVisibility(View.INVISIBLE);
            } else {
                mImageButtonIconNote.setBackgroundResource(0);
                mImageButtonIconNote.setOnClickListener(null);
                mImageButtonMoreMenu.setVisibility(View.VISIBLE);
            }

            mImageButtonMoreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noteRecyclerAdapter.getActionsListNotes().showBottomMenu(id);
                }
            });

            parent.setLongClickable(true);
            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    changeItemSelect(noteRecyclerAdapter.getActionsListNotes().selectElement(id));
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
    public NoteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.item_notes_list, parent, false);
        return new NoteRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteRecyclerAdapter.ViewHolder viewHolder, Cursor cursor) {
        cursor.moveToPosition(cursor.getPosition());
        viewHolder.setData(cursor, this);
    }

    @Override
    public boolean onLongClick(View view) {
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
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

    public Boolean isStateSelected() {
        return mStateSelected;
    }

    public void setStateSelected(Boolean mStateSelected) {
        this.mStateSelected = mStateSelected;
    }

    public ArrayList getSelectNotes() {
        return mSelectNotes;
    }

    public void setSelectNotes(ArrayList selectNotes) {
        this.mSelectNotes = selectNotes;
    }
}
