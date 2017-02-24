package com.gawk.voicenotes.fragments_main;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.adapters.NoteCursorAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotesListFragment extends Fragment implements ActionsListNotes {
    private ListView listViewAllNotes;
    private SQLiteDBHelper dbHelper;
    private NoteCursorAdapter noteCursorAdapter;

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
        return true;
    }

    @Override
    public void deleteNote(long id) {
        dbHelper.noteDelete(id);
        updateNote();
    }

    @Override
    public void selectNote(long id) {

        Log.e("GAWK_ERR","selected notes - " + String.valueOf(id));
    }
}
