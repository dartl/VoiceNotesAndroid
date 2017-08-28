package com.gawk.voicenotes.fragments_main;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.MainActivity;
import com.gawk.voicenotes.ParentActivity;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.lists_adapters.ListAdapters;
import com.gawk.voicenotes.lists_adapters.NotificationRecyclerAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.util.ArrayList;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotificationsListFragment extends FragmentParent {
    private MainActivity mainActivity;
    private RecyclerView mRecyclerView;
    private NotificationRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListAdapters mListAdapters;

    public NotificationsListFragment() {
        // Required empty public constructor
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_list_fragment, null);

        dbHelper = SQLiteDBHelper.getInstance(getActivity());
        dbHelper.connection();

        mListAdapters = new ListAdapters(view,this);
        mListAdapters.changeVisibleItemMenu(R.id.action_share_element,false);

        Cursor notificationCursor = dbHelper.getCursorAllNotification();

        /* new NoteRecycler */
        mAdapter = new NotificationRecyclerAdapter(getActivity(), notificationCursor, mListAdapters, dbHelper);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.listViewAllNotifications);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

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
        Cursor cursor = dbHelper.getCursorAllNotification();
        Log.e("GAWK_ERR","updateList() Notification. cursor.getCount() = " + cursor.getCount());
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void deleteItemList(long id, boolean stateRemoveAll, ArrayList selectItems) {
        super.deleteItemList(id, stateRemoveAll, selectItems);
        if (stateRemoveAll) {
            dbHelper.deleteAllNotificationByNote(id);
        } else {
            if (id == -1) {
                int i = 0;
                long id_temp;
                if (selectItems.size() > 0) {
                    while (!selectItems.isEmpty()) {
                        id_temp = (Long) selectItems.get(i);
                        selectItems.remove(i);
                        dbHelper.connection();
                        dbHelper.setActivity((ParentActivity) getContext());
                        dbHelper.deleteNotification(id_temp);
                    }
                }
            } else {
                dbHelper.deleteNotification(id);
            }
        }
    }

    @Override
    public void refreshSelectedList() {
        onResume();
    }
}
