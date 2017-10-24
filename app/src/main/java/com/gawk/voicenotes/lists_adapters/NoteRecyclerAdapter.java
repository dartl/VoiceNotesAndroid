package com.gawk.voicenotes.lists_adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.activities.ViewNoteActivity;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by GAWK on 24.03.2017.
 */

public class NoteRecyclerAdapter extends CursorRecyclerViewAdapter<NoteRecyclerAdapter.ViewHolder> implements View.OnLongClickListener {

    private ActionsListNotes actionsListNotes;
    private SQLiteDBHelper db;
    private Context mContext;
    private Boolean mStateSelected = false;

    public NoteRecyclerAdapter(Context context, Cursor cursor, ActionsListNotes actionsListNotes,  SQLiteDBHelper db) {
        super(context, cursor);
        this.mContext = context;
        this.actionsListNotes = actionsListNotes;
        this.db = db;
        this.db.connection();
    }

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, dateView, mTextViewGroup, mTextViewListCategory;
        ImageButton mImageButtonIconNote, mImageButtonMoreMenu;
        CardView cardView;
        View parent;
        NoteRecyclerAdapter mNoteRecyclerAdapter;

        public ViewHolder(View v) {
            super(v);
            parent = v;
            mImageButtonIconNote = v.findViewById(R.id.imageButtonIconNote);
            mImageButtonMoreMenu = v.findViewById(R.id.imageButtonMoreMenu);
            textView = v.findViewById(R.id.textViewListText);
            dateView = v.findViewById(R.id.textViewListDate);
            mTextViewListCategory = v.findViewById(R.id.textViewListCategory);
            cardView = v.findViewById(R.id.card_view);
            mTextViewGroup = v.findViewById(R.id.textViewGroup);
        }

        public void setData(final Cursor c, final NoteRecyclerAdapter noteRecyclerAdapter, SQLiteDBHelper db) {
            mNoteRecyclerAdapter = noteRecyclerAdapter;
            final int position = c.getPosition();
            final long id = noteRecyclerAdapter.getItemId(getLayoutPosition());

            changeItemSelect(noteRecyclerAdapter.getActionsListNotes().checkSelectElement(id));
            Note note = new Note(c);
            Log.e("GAWK_ERR","note = " + note.toString());
            String categoryName = db.getNameCategory(note.getCategoryId());

            mTextViewListCategory.setText(categoryName);

            textView.setText(note.getText_note());
            mImageButtonIconNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (noteRecyclerAdapter.getActionsListNotes().isStateSelected()) {
                        parent.performLongClick();
                    }
                }
            });

            mImageButtonIconNote.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
            mImageButtonIconNote.setColorFilter(mNoteRecyclerAdapter.getColorByAttr(R.attr.primaryColor500));

            if (noteRecyclerAdapter.getActionsListNotes().isStateSelected()) {
                if (noteRecyclerAdapter.getActionsListNotes().checkSelectElement(id)) {
                    mImageButtonIconNote.setImageResource(R.drawable.ic_done_white_24dp);
                    mImageButtonIconNote.setColorFilter(ContextCompat.getColor(noteRecyclerAdapter.getContext(), R.color.colorWhite));
                    mImageButtonIconNote.setBackgroundResource(R.drawable.list_item_circle_primary);
                } else {
                    mImageButtonIconNote.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
                    mImageButtonIconNote.setBackgroundResource(R.drawable.list_item_circle);
                }
                mImageButtonMoreMenu.setVisibility(View.INVISIBLE);
            } else {
                mImageButtonIconNote.setBackgroundResource(0);
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
                    intent = new Intent(v.getContext(), ViewNoteActivity.class);
                    intent.putExtra("id",id);
                    v.getContext().startActivity(intent);
                }
            });

            Date date = note.getDate();
            DateFormat dateFormat;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date.getTime());
            if (!noteRecyclerAdapter.checkDateNote(calendar)) {
                dateFormat = SimpleDateFormat.getDateInstance();
                String date_string = dateFormat.format(date);
                mTextViewGroup.setText(date_string);
                mTextViewGroup.setVisibility(View.VISIBLE);
            } else {
                mTextViewGroup.setVisibility(View.GONE);
            }
            dateFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
            dateView.setText(dateFormat.format(date));
        }

        private void changeItemSelect(boolean state) {
            if (state) {
                cardView.setBackgroundColor(mNoteRecyclerAdapter.getColorByAttr(R.attr.colorSelectListItem));
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
        View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notes, parent, false);
        return new NoteRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteRecyclerAdapter.ViewHolder viewHolder, Cursor cursor) {
        cursor.moveToPosition(cursor.getPosition());
        viewHolder.setData(cursor, this, db);
    }

    @Override
    public boolean onLongClick(View view) {
        view.setBackgroundColor(ContextCompat.getColor(getContext(),getColorByAttr(R.attr.primaryColor)));
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

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        // инициализируем сегодняшним днем
        mGroupEndDate = Calendar.getInstance();
        mGroupEndDate.set(
                mGroupEndDate.get(Calendar.YEAR),
                mGroupEndDate.get(Calendar.MONTH),
                mGroupEndDate.get(Calendar.DAY_OF_MONTH)+2,
                0,0);
        mGroupStartDate = Calendar.getInstance();
        mGroupStartDate.set(
                mGroupStartDate.get(Calendar.YEAR),
                mGroupStartDate.get(Calendar.MONTH),
                mGroupStartDate.get(Calendar.DAY_OF_MONTH)+1,
                0,0);
        return super.swapCursor(newCursor);
    }
}
