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
import android.widget.ListView;

import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.MainActivity;
import com.gawk.voicenotes.ParentActivity;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.adapters.NotificationRecyclerAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.util.ArrayList;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotificationsListFragment extends FragmentParent implements ActionsListNotes {
    private MainActivity mainActivity;
    private RecyclerView mRecyclerView;
    private NotificationRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList selectNotification = new ArrayList<Long>();

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

        Cursor notificationCursor = dbHelper.getCursorAllNotification();

        /* new NoteRecycler */
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new NotificationRecyclerAdapter(getActivity(), notificationCursor, this, dbHelper);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.listViewAllNotifications);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotification();
    }

    public boolean updateNotification() {
        dbHelper.setActivity((ParentActivity)getContext());
        dbHelper.deleteAllOldNotification();
        Cursor notificationCursor = dbHelper.getCursorAllNotification();
        mAdapter.changeCursor(notificationCursor);
        return true;
    }

    // state: 0 - delete one notification, 1 - delete all notification, 2 - delete all notification by note
    public void deleteElement(long id, int state) {
        switch (state) {
            case 0:
                dbHelper.deleteNotification(id);
                updateNotification();
                break;
            case 1:
                int i = 0;
                long id_temp;
                if (selectNotification.size() > 0) {
                    while (!selectNotification.isEmpty()) {
                        id_temp = (Long) selectNotification.get(i);
                        selectNotification.remove(i);
                        dbHelper.connection();
                        dbHelper.setActivity((ParentActivity) getContext());
                        dbHelper.deleteNotification(id_temp);
                    }
                    updateNotification();
                }
                break;
            case 2:
                dbHelper.deleteAllNotificationByNote(id);
                updateNotification();
                break;
            default:
                break;
        }
    }

    @Override
    public void showDialogDelete(final long _id,final int state) {
        if (selectNotification.size() > 0 ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        } else {
            Snackbar.make(getView(), getResources().getString(R.string.main_view_error_select), Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void selectNote(long id, boolean checked) {
    }

    @Override
    public void selectNotification(long id, boolean checked) {
        if (checked) {
            selectNotification.add(id);
        } else {
            selectNotification.remove(id);
        }
    }
}
