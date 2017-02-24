package com.gawk.voicenotes.fragments_main;

import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

    @Override
    public void deleteNote(long id) {
        dbHelper.noteDelete(id);
        updateNote();
    }

    @Override
    public void selectNote(long id, boolean checked) {
        if (checked) {
            selectNotes.add(id);
        } else {
            selectNotes.remove(id);
        }
    }

    public void deleteSelectedNote() {
        int i = 0;
        long id;
        if (selectNotes.size() > 0) {
            while (!selectNotes.isEmpty()) {
                id = (Long) selectNotes.get(i);
                selectNotes.remove(i);
                dbHelper.noteDelete(id);
            }
            updateNote();
        }
    }
}
