package com.gawk.voicenotes.fragments_main;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.MainActivity;
import com.gawk.voicenotes.NewNote;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.lists_adapters.NoteRecyclerAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotesListFragment extends FragmentParent implements ActionsListNotes {
    private MainActivity mainActivity;
    private RelativeLayout mRelativeLayoutBottomMenu;
    private ImageButton mImageButtonShare, mImageButtonDelete;

    private ArrayList selectNotes = new ArrayList<Long>();

    private RecyclerView mRecyclerView;
    private NoteRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public NotesListFragment() {
        // Required empty public constructor
    }


    public void setMainActivity(MainActivity mainActivity) {
        // Required empty public constructor
        this.mainActivity = mainActivity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_list_fragment, null);

        dbHelper = SQLiteDBHelper.getInstance(getActivity());
        dbHelper.connection();

        Cursor noteCursor = dbHelper.getCursorAllNotes();

        mRelativeLayoutBottomMenu = view.findViewById(R.id.relativeLayoutBottomMenu);
        mImageButtonShare = view.findViewById(R.id.imageButtonShare);
        mImageButtonDelete = view.findViewById(R.id.imageButtonDelete);
        mImageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDelete();
            }
        });

        mImageButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareSelectNotes();
            }
        });

        /* new NoteRecycler */
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new NoteRecyclerAdapter(getActivity(), noteCursor, this);

        mRecyclerView = view.findViewById(R.id.listViewAllNotes);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab =  view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewNote.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNote(dbHelper.getCursorAllNotes());
    }

    public boolean updateNote(Cursor noteCursor) {
        mAdapter.changeCursor(noteCursor);
        NavigationView navigationView =  getActivity().findViewById(R.id.nav_view_menu);
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.menu_notes_list).getActionView();
        view.setText(mAdapter.getItemCount() > 0 ? String.valueOf(mAdapter.getItemCount()) : null);
        return true;
    }

    public void deleteSelectNotes() {
        dbHelper.connection();
        long id_temp;
        while (!selectNotes.isEmpty()) {
            id_temp = (Long) selectNotes.get(0);
            selectNotes.remove(0);
            dbHelper.noteDelete(id_temp);
            deleteNotifications(id_temp);
        }
        updateNote(dbHelper.getCursorAllNotes());
        changeBottomMenu();
    }

    public void deleteNotifications(long id) {
        if (mainActivity != null) {
            NotificationsListFragment fragment = mainActivity.getFragment(1);
            fragment.deleteElement(id,2);
        }
    }

    public void shareSelectNotes() {
        String mShareText = "";
        Cursor cursor;
        Note note;
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        for (int i = 0; i < selectNotes.size();i++) {
            cursor = dbHelper.getNoteById((long)selectNotes.get(i));
            cursor.moveToFirst();
            note = new Note(cursor);
            mShareText += dateFormat.format(note.getDate()) + "\n";
            mShareText += note.getText_note()+ "\n\n";
        }
        mShareText += getText(R.string.main_share_text);
        mShareText += "\n";
        mShareText += getText(R.string.app_name) + " - https://play.google.com/store/apps/details?id=com.gawk.voicenotes";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, mShareText);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getText(R.string.main_share)));
    }

    @Override
    public boolean selectNote(long id) {
        if (selectNotes.contains(id)) {
            selectNotes.remove(id);
            changeBottomMenu();
            return false;
        } else {
            selectNotes.add(id);
            changeBottomMenu();
            return true;
        }
    }

    @Override
    public boolean checkSelectNote(long id) {
        return selectNotes.contains(id);
    }

    @Override
    public void selectNotification(long id) {}

    public void showDialogDelete() {
        if (selectNotes.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.dialogDeleteMessage)
                    .setTitle(R.string.dialogDeleteTitle);

            // Add the buttons
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteSelectNotes();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            if (getActivity().getCurrentFocus() != null) {
                Snackbar.make(getActivity().getCurrentFocus(), getResources().getString(R.string.main_view_error_select), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void search(String text) {
        dbHelper.connection();
        updateNote(dbHelper.getCursorAllNotes(text));
    }

    private void changeBottomMenu() {
        mAdapter.setSelectNotes(selectNotes);
        if (selectNotes.size() > 0) {
            mAdapter.setStateSelected(true);
            mRelativeLayoutBottomMenu.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            mRelativeLayoutBottomMenu.requestLayout();
            mRelativeLayoutBottomMenu.animate().translationY(0);
        } else {
            mAdapter.setStateSelected(false);
            mRelativeLayoutBottomMenu.animate().translationY(mRelativeLayoutBottomMenu.getHeight());
            mRelativeLayoutBottomMenu.animate().withEndAction(new Runnable() {
                @Override
                public void run() {
                    mRelativeLayoutBottomMenu.getLayoutParams().height= 0;
                    mRelativeLayoutBottomMenu.requestLayout();
                }
            });
        }
        mAdapter.notifyDataSetChanged();
    }
}
