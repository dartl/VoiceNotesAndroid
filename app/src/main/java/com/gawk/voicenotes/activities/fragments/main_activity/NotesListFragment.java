package com.gawk.voicenotes.activities.fragments.main_activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.activities.MainActivity;
import com.gawk.voicenotes.activities.CreateNoteActivity;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.CategoriesSpinner;
import com.gawk.voicenotes.activities.fragments.main_activity.adapters.ListenerSelectFilterCategory;
import com.gawk.voicenotes.adapters.lists_adapters.ListAdapters;
import com.gawk.voicenotes.adapters.lists_adapters.NoteRecyclerAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotesListFragment extends FragmentParent implements ListenerSelectFilterCategory {
    private MainActivity mainActivity;
    private ListAdapters mListAdapters;
    private NoteRecyclerAdapter mAdapter;
    private RelativeLayout mRelativeLayoutEmptyNotes;

    private Spinner mSpinnerFilter;
    private CategoriesSpinner mCategoriesSpinner;
    private RelativeLayout mRelativeLayoutFilter;
    private ImageButton mImageButtonCloseFilter;
    private String mSearchText;
    private long mFilterCategory = -1;

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
        View view = inflater.inflate(R.layout.activity_main_fragment_notes, null);

        mRelativeLayoutEmptyNotes = view.findViewById(R.id.relativeLayoutEmptyNotes);
        mSpinnerFilter = view.findViewById(R.id.spinnerFilter);
        mRelativeLayoutFilter = view.findViewById(R.id.relativeLayoutFilter);
        mImageButtonCloseFilter = view.findViewById(R.id.imageButtonCloseFilter);

        mImageButtonCloseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter();
            }
        });

        mCategoriesSpinner = new CategoriesSpinner(dbHelper, getContext(), mSpinnerFilter, -1);
        mCategoriesSpinner.setListenerSelectFilterCategory(this);

        dbHelper = SQLiteDBHelper.getInstance(getActivity());
        dbHelper.connection();

        Cursor noteCursor = dbHelper.getCursorAllNotes();

        mListAdapters = new ListAdapters(view,this,getActivity());
        mListAdapters.changeVisibleItemMenu(R.id.action_share_element,true);
        mListAdapters.changeVisibleItemSelectedMenu(R.id.imageButtonShare,View.VISIBLE);

        /* new NoteRecycler */
        mAdapter = new NoteRecyclerAdapter(getActivity(), noteCursor, mListAdapters, dbHelper);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = view.findViewById(R.id.listViewAllNotes);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab =  view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void updateList() {
        super.updateList();
        mAdapter.changeCursor(dbHelper.getCursorAllNotes());
        NavigationView navigationView =  getActivity().findViewById(R.id.nav_view_menu);
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.menu_notes_list).getActionView();
        view.setText(mAdapter.getItemCount() > 0 ? String.valueOf(mAdapter.getItemCount()) : null);
        if (mAdapter.getItemCount() > 0) {
            mRelativeLayoutEmptyNotes.setVisibility(View.GONE);
        } else {
            mRelativeLayoutEmptyNotes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void deleteItemList(long id, boolean stateRemoveAllNotification, ArrayList selectItems) {
        super.deleteItemList(id,stateRemoveAllNotification,selectItems);
        dbHelper.connection();
        long id_temp;
        if (id == -1) {
            while (!selectItems.isEmpty()) {
                id_temp = (Long) selectItems.get(0);
                selectItems.remove(0);
                dbHelper.noteDelete(id_temp);
            }
        } else {
            dbHelper.noteDelete(id);
        }
        updateList();
        if (mainActivity != null && mainActivity.getFragment(1) != null) {
            mainActivity.getFragment(1).updateList();
        }
        if (mainActivity != null) {
            mainActivity.refreshNavHeader();
        }
    }

    @Override
    public void shareItemList(long id, ArrayList selectItems) {
        super.shareItemList(id,selectItems);
        String mShareText = "";
        Cursor cursor;
        Note note;
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        if (id == -1) {
            for (int i = 0; i < selectItems.size();i++) {
                cursor = dbHelper.getNoteById((long)selectItems.get(i));
                cursor.moveToFirst();
                note = new Note(cursor);
                mShareText += dateFormat.format(note.getDate()) + "\n";
                mShareText += note.getText_note()+ "\n\n";
            }
        } else {
            cursor = dbHelper.getNoteById(id);
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
    public void refreshSelectedList() {
        onResume();
    }

    @Override
    public void search(String text) {
        if (dbHelper != null) {
            dbHelper.connection();
            mSearchText = text;
            if (mSearchText != null && mSearchText.equals("")) {
                mSearchText = null;
            }
            mAdapter.changeCursor(dbHelper.getCursorAllNotes(mSearchText,mFilterCategory));
        }
    }

    public void filter() {
        if (mRelativeLayoutFilter.getVisibility() == View.VISIBLE) {
            closeFilter();
        } else {
            mRelativeLayoutFilter.setVisibility(View.VISIBLE);
            mSpinnerFilter.performClick();
        }
    }

    @Override
    public void changeCategoryFilter(long newCategoryId) {
        mFilterCategory = newCategoryId;
        mAdapter.changeCursor(dbHelper.getCursorAllNotes(mSearchText,mFilterCategory));
    }

    public void closeFilter() {
        mFilterCategory = -1;
        mRelativeLayoutFilter.setVisibility(View.GONE);
        mAdapter.changeCursor(dbHelper.getCursorAllNotes(mSearchText,mFilterCategory));
        mCategoriesSpinner.refresh();
    }
}
