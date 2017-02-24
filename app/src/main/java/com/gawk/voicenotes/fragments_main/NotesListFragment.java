package com.gawk.voicenotes.fragments_main;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.adapters.NoteCursorAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotesListFragment extends Fragment implements ActionsListNotes {
    private ListView listViewAllNotes;
    private SQLiteDBHelper dbHelper;
    private NoteCursorAdapter noteCursorAdapter;

    private ArrayList selectNotes = new ArrayList<Long>();

    public NotesListFragment() {
        // Required empty public constructor
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

        listViewAllNotes = (ListView) view.findViewById(R.id.listViewAllNotes);

        noteCursorAdapter = new NoteCursorAdapter(getActivity(), noteCursor, true, this);
        Log.d("GAWK_ERR","Идет процесс");
        listViewAllNotes.setAdapter(noteCursorAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNote();
    }

    public boolean updateNote() {
        Cursor noteCursor = dbHelper.getCursorAllNotes();
        noteCursorAdapter.changeCursor(noteCursor);
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.menu_notes_list).getActionView();
        view.setText(noteCursorAdapter.getCount() > 0 ? String.valueOf(noteCursorAdapter.getCount()) : null);
        return true;
    }

    // state: 0 - delete one note, 1 - delete all note
    public void deleteElement(long id, int state) {
        switch (state) {
            case 0:
                dbHelper.noteDelete(id);
                updateNote();
                break;
            case 1:
                int i = 0;
                long id_temp;
                if (selectNotes.size() > 0) {
                    while (!selectNotes.isEmpty()) {
                        id_temp = (Long) selectNotes.get(i);
                        selectNotes.remove(i);
                        dbHelper.noteDelete(id_temp);
                    }
                    updateNote();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void selectNote(long id, boolean checked) {
        if (checked) {
            selectNotes.add(id);
        } else {
            selectNotes.remove(id);
        }
    }

    @Override
    public void showDialogDelete(final long _id, final int state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialogDeleteMessage)
                .setTitle(R.string.dialogDeleteTitle);

        // Add the buttons
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteElement(_id,state);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
