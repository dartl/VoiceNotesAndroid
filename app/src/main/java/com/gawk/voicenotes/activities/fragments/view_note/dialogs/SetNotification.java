package com.gawk.voicenotes.activities.fragments.view_note.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.main_activity.NotificationsListFragment;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.date_and_time.DateAndTimeCombine;
import com.gawk.voicenotes.adapters.listeners.TimePickerReturn;
import com.gawk.voicenotes.models.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by GAWK on 26.08.2017.
 */

public class SetNotification extends DialogFragment implements TimePickerReturn {
    private Dialog mDlg;
    private Switch mSwitchNotification, mSwitchSound, mSwitchVibrate, mSwitchRepeat;
    private View mView;
    private ArrayList<View> allChildren;
    private AppCompatButton selectTime, mButtonSave, mButtonClose;
    private TextView textViewNowDate;
    private Calendar dateNotification = Calendar.getInstance();;
    private DateAndTimeCombine mDateAndTimeCombine;
    private boolean checkError, checkNotification = false;
    private NotificationsListFragment mNotificationsListFragment;
    public SQLiteDBHelper dbHelper;

    private Notification notification = new Notification();

    public SetNotification(long id_note) {notification.setId_note(id_note);}

    public SetNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.activity_create_note_fragment_notification, null);
        builder.setView(mView);

        dbHelper = SQLiteDBHelper.getInstance(getContext());
        dbHelper.connection();

        RelativeLayout notificationLayout =  mView.findViewById(R.id.notificationLayout);

        mSwitchNotification = mView.findViewById(R.id.switchNotification);
        selectTime = mView.findViewById(R.id.buttonSelectTime);
        textViewNowDate = mView.findViewById(R.id.textViewNowDate);
        mSwitchSound = mView.findViewById(R.id.switchSound);
        mSwitchVibrate = mView.findViewById(R.id.switchVibrate);
        mSwitchRepeat = mView.findViewById(R.id.switchRepeat);
        mButtonSave = mView.findViewById(R.id.buttonSave);
        mButtonClose = mView.findViewById(R.id.buttonClose);


        allChildren = getAllChildren(notificationLayout);
        for (int i = 0; i < allChildren.size(); i++) {
            allChildren.get(i).setEnabled(true);
        }

        mSwitchNotification.setVisibility(View.GONE);
        selectTime.setVisibility(View.GONE);

        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationsListFragment.saveNotification(notification);
                dismiss();
            }
        });

        mSwitchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notification.setSound(isChecked);
            }
        });

        mSwitchVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notification.setShake(isChecked);
            }
        });

        mSwitchRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notification.setRepeat(isChecked);
            }
        });

        if (notification.getId() != -1) {
            dateNotification.setTimeInMillis(notification.getDate().getTime());
        }

        setNotificationTime();

        mDlg = builder.create();
        return mDlg;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        showTimePickerDialog();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void showTimePickerDialog() {
        if (mDateAndTimeCombine == null) {
            if (notification.getId() != -1) {
                mDateAndTimeCombine = new DateAndTimeCombine(this, notification.getDate().getTime());
            } else {
                mDateAndTimeCombine = new DateAndTimeCombine(this);
            }
        }
        mDateAndTimeCombine.show(getFragmentManager());
    }

    private void setNotificationTime() {
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        textViewNowDate.setText(dateFormat.format(dateNotification.getTime()));
        notification.setDate(dateNotification.getTime());
    }

    @Override
    public void setTimeAndDate(Calendar calendar) {
        checkError = true;
        dateNotification = calendar;
        setNotificationTime();
    }

    @Override
    public void fail() {
        mNotificationsListFragment.failSetNotification();
        checkError = false;
        mSwitchNotification.setChecked(false);
        dismiss();
    }

    public Notification getNotification() {
        if (checkError && checkNotification) {
            return notification;
        }
        return null;
    }

    public boolean haveNotification() {
        return (checkError && checkNotification);
    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    public NotificationsListFragment getNoteView() {
        return mNotificationsListFragment;
    }

    public void setNoteView(NotificationsListFragment mNotificationsListFragment) {
        this.mNotificationsListFragment = mNotificationsListFragment;
    }
}