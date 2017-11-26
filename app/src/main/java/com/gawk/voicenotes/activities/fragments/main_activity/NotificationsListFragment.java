package com.gawk.voicenotes.activities.fragments.main_activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.activities.ParentActivity;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.view_note.dialogs.SetNotification;
import com.gawk.voicenotes.adapters.NotificationAdapter;
import com.gawk.voicenotes.adapters.lists_adapters.ListAdapters;
import com.gawk.voicenotes.adapters.lists_adapters.NotificationRecyclerAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

import java.util.ArrayList;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotificationsListFragment extends FragmentParent  {
    private RecyclerView mRecyclerView;
    private RelativeLayout mRelativeLayoutEmptyNotifications;
    private FloatingActionButton mFloatingActionButton;
    private NotificationsListFragment mNotificationsListFragment;

    private NotificationRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ListAdapters mListAdapters;
    private SetNotification mSetNotification;
    private Note note;
    private long mNoteActiveId;
    private boolean mIsNotificationsListFromViewNote = false;

    public NotificationsListFragment() {
        mNoteActiveId = -1;
        mNotificationsListFragment = this;
        // Required empty public constructor
    }

    public NotificationsListFragment(long id) {
        mNoteActiveId = id;
        mNotificationsListFragment = this;
        mIsNotificationsListFromViewNote = true;
        Log.e("GAWK_ERR","mIsNotificationsList = " + mIsNotificationsListFromViewNote);
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_fragment_notifications, null);

        mRelativeLayoutEmptyNotifications = view.findViewById(R.id.relativeLayoutEmptyNotifications);
        mFloatingActionButton = view.findViewById(R.id.floatingActionButtonAdd);

        dbHelper = SQLiteDBHelper.getInstance(getActivity());
        dbHelper.connection();

        mListAdapters = new ListAdapters(view,this,getActivity());

        Cursor notificationCursor = getCursorNotifications();

        /* new NoteRecycler */
        mAdapter = new NotificationRecyclerAdapter(getActivity(), notificationCursor, mListAdapters, dbHelper);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.listViewAllNotifications);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if(mIsNotificationsListFromViewNote) {
            mFloatingActionButton.setVisibility(View.VISIBLE);
            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSetNotification = new SetNotification(mNoteActiveId);
                    mSetNotification.setNoteView(mNotificationsListFragment);
                    mSetNotification.show(getFragmentManager(),"SetNotification");
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("GAWK_ERR", "onResume NotificationsListFragment()");
        updateList();
    }

    @Override
    public void updateList() {
        super.updateList();
        dbHelper.deleteAllOldNotification();
        Cursor cursor = getCursorNotifications();
        Log.e("GAWK_ERR","updateList() Notification. cursor.getCount() = " + cursor.getCount());
        mAdapter.changeCursor(cursor);
        if (mAdapter.getItemCount() > 0) {
            mRelativeLayoutEmptyNotifications.setVisibility(View.GONE);
        } else {
            mRelativeLayoutEmptyNotifications.setVisibility(View.VISIBLE);
        }
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
                        dbHelper.deleteNotification(id_temp);
                    }
                }
            } else {
                dbHelper.deleteNotification(id);
            }
        }
    }

    @Override
    public void refreshSelectedList(int position) {
        onResume();
    }

    public void saveNotification(Notification notification) {
        notification.setId(dbHelper.saveNotification(notification));
        NotificationAdapter notificationAdapter = new NotificationAdapter(getContext());
        notificationAdapter.restartNotify(new Note(dbHelper.getNoteById(mNoteActiveId)), notification);
        updateList();
    }

    public void failSetNotification() {
        Snackbar.make(getView(), getString(R.string.new_note_error_date), Snackbar.LENGTH_LONG).show();
    }

    private Cursor getCursorNotifications() {
        if (mNoteActiveId != -1) {
            return dbHelper.getAllNotificationByNote(mNoteActiveId);
        } else {
            return dbHelper.getCursorAllNotification();
        }
    }
}
