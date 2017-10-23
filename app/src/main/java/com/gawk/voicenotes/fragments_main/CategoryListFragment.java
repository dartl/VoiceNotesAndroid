package com.gawk.voicenotes.fragments_main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.MainActivity;
import com.gawk.voicenotes.NewNote;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.lists_adapters.ListAdapters;
import com.gawk.voicenotes.lists_adapters.NoteRecyclerAdapter;
import com.gawk.voicenotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by GAWK on 23.10.2017.
 */

public class CategoryListFragment extends FragmentParent{
    private MainActivity mainActivity;
    private ListAdapters mListAdapters;
    private RelativeLayout mRelativeLayoutEmptyCategory;

    public CategoryListFragment() {
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
        View view = inflater.inflate(R.layout.categories, null);

        mRelativeLayoutEmptyCategory = view.findViewById(R.id.relativeLayoutEmptyCategory);

        dbHelper = SQLiteDBHelper.getInstance(getActivity());
        dbHelper.connection();

        Cursor noteCursor = dbHelper.getCursorAllNotes();

        mListAdapters = new ListAdapters(view,this,getActivity());

        /* new NoteRecycler */
        //mAdapter = new NoteRecyclerAdapter(getActivity(), noteCursor, mListAdapters);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerViewCategories);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab =  view.findViewById(R.id.fabAddCategory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        /*mAdapter.changeCursor(dbHelper.getCursorAllNotes());
        NavigationView navigationView =  getActivity().findViewById(R.id.nav_view_menu);
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.menu_notes_list).getActionView();
        view.setText(mAdapter.getItemCount() > 0 ? String.valueOf(mAdapter.getItemCount()) : null);
        if (mAdapter.getItemCount() > 0) {
            mRelativeLayoutEmptyNotes.setVisibility(View.GONE);
        } else {
            mRelativeLayoutEmptyNotes.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void deleteItemList(long id, boolean stateRemoveAllNotification, ArrayList selectItems) {
        super.deleteItemList(id,stateRemoveAllNotification,selectItems);
        dbHelper.connection();
    }

    @Override
    public void shareItemList(long id, ArrayList selectItems) {
        super.shareItemList(id,selectItems);
    }

    @Override
    public void refreshSelectedList() {
        onResume();
    }

    @Override
    public void search(String text) {
        if (dbHelper != null) {
            dbHelper.connection();
            //mAdapter.changeCursor(dbHelper.getCursorAllNotes(text));
        }
    }
}
