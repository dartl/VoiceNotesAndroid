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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.MainActivity;
import com.gawk.voicenotes.NewNote;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.adapters.NoteRecyclerAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.util.ArrayList;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotesListFragment extends FragmentParent implements ActionsListNotes {
    private MainActivity mainActivity;

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
        Log.e("GAWK_ERR","deleteSelectNotes = " + selectNotes.toString());
        while (!selectNotes.isEmpty()) {
            id_temp = (Long) selectNotes.get(0);
            selectNotes.remove(0);
            dbHelper.noteDelete(id_temp);
            deleteNotifications(id_temp);
        }
        Log.e("GAWK_ERR","deleteSelectNotes = " + selectNotes.toString());
        updateNote(dbHelper.getCursorAllNotes());
    }

    public void deleteNotifications(long id) {
        if (mainActivity != null) {
            NotificationsListFragment fragment = mainActivity.getFragment(1);
            fragment.deleteElement(id,2);
        }
    }

    @Override
    public boolean selectNote(long id) {
        if (selectNotes.contains(id)) {
            Log.e("GAWK_ERR","selectNotes REMOVE before  = " + selectNotes.toString());
            selectNotes.remove(id);
            Log.e("GAWK_ERR","selectNotes REMOVE after = " + selectNotes.toString());
            return false;
        } else {
            Log.e("GAWK_ERR","selectNotes ADD before  = " + selectNotes.toString());
            selectNotes.add(id);
            Log.e("GAWK_ERR","selectNotes ADD after = " + selectNotes.toString());
            return true;
        }
    }

    @Override
    public boolean checkSelectNote(long id) {
        return selectNotes.contains(id);
    }

    @Override
    public void selectNotification(long id) {}

    @Override
    public void showDialogDelete(final long _id, final int state) {
        if (selectNotes.size() > 0 || state == 0) {
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
}
