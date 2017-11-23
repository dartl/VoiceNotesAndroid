package com.gawk.voicenotes.activities.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gawk.voicenotes.adapters.ActionMenuBottom;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.util.ArrayList;

/**
 * Created by GAWK on 05.03.2017.
 */

public class FragmentParent extends Fragment implements ActionMenuBottom {
    public SQLiteDBHelper dbHelper;

    public FragmentParent() {
        this.dbHelper = SQLiteDBHelper.getInstance(getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dbHelper = SQLiteDBHelper.getInstance(getContext());
    }

    // функция поиска
    public void search(String text) {}


    @Override
    public void updateList() {

    }

    @Override
    public void deleteItemList(long id, boolean stateRemoveAllNotification, ArrayList selectItems) {

    }

    @Override
    public void shareItemList(long id, ArrayList selectItems) {

    }

    @Override
    public void editedItemList(long id) {

    }

    @Override
    public void refreshSelectedList(int position) {

    }
}
