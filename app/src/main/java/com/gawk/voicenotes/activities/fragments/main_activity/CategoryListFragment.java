package com.gawk.voicenotes.activities.fragments.main_activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.activities.MainActivity;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.lists_adapters.CategoriesRecyclerAdapter;
import com.gawk.voicenotes.adapters.lists_adapters.ListAdapters;
import com.gawk.voicenotes.models.Category;
import com.gawk.voicenotes.windows.AddNewCategory;

import java.util.ArrayList;

/**
 * Created by GAWK on 23.10.2017.
 */

public class CategoryListFragment extends FragmentParent{
    private MainActivity mainActivity;
    private ListAdapters mListAdapters;
    private CategoriesRecyclerAdapter mAdapter;
    private RelativeLayout mRelativeLayoutEmptyCategory;
    private AddNewCategory mAddNewCategory;
    private CategoryListFragment mCategoryListFragment;
    private NotesListFragment mNotesListFragment;

    public CategoryListFragment() {
        // Required empty public constructor
        mCategoryListFragment = this;
    }

    public CategoryListFragment(NotesListFragment notesListFragment) {
        // Required empty public constructor
        mCategoryListFragment = this;
        mNotesListFragment = notesListFragment;
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
        View view = inflater.inflate(R.layout.activity_main_fragment_categories, null);

        mRelativeLayoutEmptyCategory = view.findViewById(R.id.relativeLayoutEmptyCategory);

        dbHelper = SQLiteDBHelper.getInstance(getActivity());
        dbHelper.connection();

        Cursor categoryCursor = dbHelper.getCursorAllCategories();

        mListAdapters = new ListAdapters(view,this,getActivity());
        mListAdapters.changeVisibleItemMenu(R.id.action_edited_element,true);

        /* new NoteRecycler */
        mAdapter = new CategoriesRecyclerAdapter(getActivity(), categoryCursor, mListAdapters);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerViewCategories);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddCategory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddNewCategory = new AddNewCategory(mCategoryListFragment);
                mAddNewCategory.show(getActivity().getFragmentManager(),"AddNewCategory");
            }
        });
        return view;
    }

    public void saveCategory(Category category, int action) {
        dbHelper.saveCategory(category, action);
        updateList();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void updateList() {
        super.updateList();
        mAdapter.changeCursor(dbHelper.getCursorAllCategories());
        if (mAdapter.getItemCount() > 0) {
            mRelativeLayoutEmptyCategory.setVisibility(View.GONE);
        } else {
            mRelativeLayoutEmptyCategory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void deleteItemList(long id, boolean stateRemoveAllNotification, ArrayList selectItems) {
        super.deleteItemList(id,stateRemoveAllNotification,selectItems);
        dbHelper.connection();
        if (id == -1) {
            long id_temp;
            while (!selectItems.isEmpty()) {
                id_temp = (Long) selectItems.get(0);
                selectItems.remove(0);
                dbHelper.removeCategory(id_temp);
            }
        } else {
            dbHelper.removeCategory(id);
        }
        updateList();
        mNotesListFragment.updateList();
    }

    @Override
    public void editedItemList(long id) {
        Category category =  new Category(id, dbHelper.getNameCategory(id));
        mAddNewCategory = new AddNewCategory(category,mCategoryListFragment);
        mAddNewCategory.show(getActivity().getFragmentManager(),"AddNewCategory");
    }

    @Override
    public void shareItemList(long id, ArrayList selectItems) {
        super.shareItemList(id,selectItems);
    }

    @Override
    public void refreshSelectedList(int position) {
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
