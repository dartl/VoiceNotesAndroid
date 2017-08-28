package com.gawk.voicenotes;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.util.ArrayList;

/**
 * Created by GAWK on 05.03.2017.
 */

public class FragmentParent extends Fragment {
    public SQLiteDBHelper dbHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = SQLiteDBHelper.getInstance(getContext());
        dbHelper.connection();
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelper.connection();
    }

    @Override
    public void onPause() {
        super.onPause();
        dbHelper.disconnection();
    }

    @Override
    public void onStop() {
        super.onStop();
        dbHelper.disconnection();
    }

    // функция поиска
    public void search(String text) {}

    public void updateList() {}

    public void deleteItemList(long id, boolean stateRemoveAllNotification, ArrayList selectItems) {}

    public void shareItemList(long id, ArrayList selectItems) {}

    public void refreshSelectedList() {}
}
