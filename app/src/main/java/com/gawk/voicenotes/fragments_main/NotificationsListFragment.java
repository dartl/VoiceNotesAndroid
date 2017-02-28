package com.gawk.voicenotes.fragments_main;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.NotificationCursorAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;

import java.util.ArrayList;

/**
 * Created by GAWK on 02.02.2017.
 */

public class NotificationsListFragment extends Fragment {
    private ListView listViewAllNotification;
    private SQLiteDBHelper dbHelper;
    private NotificationCursorAdapter notificationCursorAdapter;

    private ArrayList selectNotification = new ArrayList<Long>();

    public NotificationsListFragment() {
        // Required empty public constructor
    }

    String data[] = new String[] { "one", "two", "three", "four" };

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
        notificationCursorAdapter = new NotificationCursorAdapter(getActivity(), notificationCursor, true);

        listViewAllNotification = (ListView) view.findViewById(R.id.listViewAllNotifications);
        listViewAllNotification.setAdapter(notificationCursorAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotification();
    }

    public boolean updateNotification() {
        Cursor notificationCursor = dbHelper.getCursorAllNotification();
        notificationCursorAdapter.changeCursor(notificationCursor);
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
}
