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
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.lists_adapters.NotificationRecyclerAdapter;
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
    private BottomSheet mBottomMenu;
    private RelativeLayout mRelativeLayoutBottomMenu;
    private ImageButton mImageButtonDelete;
    private TextView mTextViewNoteSelectCount;

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

        mRelativeLayoutBottomMenu = view.findViewById(R.id.relativeLayoutBottomMenu);
        mTextViewNoteSelectCount = view.findViewById(R.id.textViewNoteSelectCount);
        mImageButtonDelete = view.findViewById(R.id.imageButtonDelete);
        mImageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDelete(-1);
            }
        });

        /* new NoteRecycler */
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new NotificationRecyclerAdapter(getActivity(), notificationCursor, this, dbHelper);

        mRecyclerView = view.findViewById(R.id.listViewAllNotifications);
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

    public void deleteElement(long id, boolean stateRemoveAll) {
        if (stateRemoveAll) {
            dbHelper.deleteAllNotificationByNote(id);
        } else {
            if (id == -1) {
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
                }
            } else {
                dbHelper.deleteNotification(id);
            }
        }
        updateNotification();
        changeBottomMenu();
    }

    public void showDialogDelete(final long _id) {
        if (selectNotification.size() > 0|| _id != -1 ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.dialogDeleteMessage)
                    .setTitle(R.string.dialogDeleteTitle);

            // Add the buttons
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteElement(_id,false);
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
    public boolean selectElement(long id) {
        if (selectNotification.contains(id)) {
            selectNotification.remove(id);
            changeBottomMenu();
            return false;
        } else {
            selectNotification.add(id);
            changeBottomMenu();
            return true;
        }
    }

    @Override
    public boolean checkSelectElement(long id) {
        return selectNotification.contains(id);
    }

    @Override
    public void showBottomMenu(final long id) {
        mBottomMenu = new BottomSheet.Builder(getActivity()).title(getText(R.string.main_action_element)).sheet(R.menu.menu_list_actions).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.action_remove_element:
                        showDialogDelete(id);
                        break;
                }
            }
        }).show();
    }

    private void changeBottomMenu() {
        mAdapter.setSelectNotes(selectNotification);
        if (selectNotification.size() > 0) {
            mAdapter.setStateSelected(true);
            mRelativeLayoutBottomMenu.getLayoutParams().height = 160;
            mRelativeLayoutBottomMenu.requestLayout();
            mRelativeLayoutBottomMenu.animate().translationY(0);
        } else {
            mAdapter.setStateSelected(false);
            mRelativeLayoutBottomMenu.animate().translationY(mRelativeLayoutBottomMenu.getHeight());
            mRelativeLayoutBottomMenu.animate().withEndAction(new Runnable() {
                @Override
                public void run() {
                    mRelativeLayoutBottomMenu.getLayoutParams().height= 0;
                    mRelativeLayoutBottomMenu.requestLayout();
                }
            });
        }
        mAdapter.notifyDataSetChanged();
        mTextViewNoteSelectCount.setText(selectNotification.size() + " " + getText(R.string.main_selected_element));
    }
}
