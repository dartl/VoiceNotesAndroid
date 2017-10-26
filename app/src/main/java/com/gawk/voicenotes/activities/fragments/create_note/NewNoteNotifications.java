package com.gawk.voicenotes.activities.fragments.create_note;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.listeners.TimePickerReturn;
import com.gawk.voicenotes.adapters.date_and_time.DateAndTimeCombine;
import com.gawk.voicenotes.models.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteNotifications extends FragmentParent implements TimePickerReturn {

    private Switch switchNotification, mSwitchSound, mSwitchVibrate, mSwitchRepeat;
    private RelativeLayout notificationLayout;
    private AppCompatButton selectTime, mButtonSave, mButtonClose;
    private TextView textViewNowDate;

    private Calendar dateNotification;
    private DateAndTimeCombine mDateAndTimeCombine;
    private boolean checkError, checkNotification = false;
    private View mView;
    private ArrayList<View> allChildren;

    private final Notification notification = new Notification();

    public NewNoteNotifications() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_create_note_fragment_notification, null);

        switchNotification =  mView.findViewById(R.id.switchNotification);
        notificationLayout =  mView.findViewById(R.id.notificationLayout);
        selectTime = mView.findViewById(R.id.buttonSelectTime);
        textViewNowDate = mView.findViewById(R.id.textViewNowDate);
        mSwitchSound = mView.findViewById(R.id.switchSound);
        mSwitchVibrate = mView.findViewById(R.id.switchVibrate);
        mSwitchRepeat = mView.findViewById(R.id.switchRepeat);
        mButtonSave = mView.findViewById(R.id.buttonSave);
        mButtonClose = mView.findViewById(R.id.buttonClose);

        mButtonSave.setVisibility(View.GONE);
        mButtonClose.setVisibility(View.GONE);
        allChildren = getAllChildren(notificationLayout);
        for (int i = 0; i < allChildren.size(); i++) {
            allChildren.get(i).setEnabled(false);
        }

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !checkError) {
                    showTimePickerDialog();
                }
                for (int i = 0; i < allChildren.size(); i++) {
                    allChildren.get(i).setEnabled(isChecked);
                }
                checkNotification = isChecked;
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

        dateNotification = Calendar.getInstance();
        setNotificationTime();
        return mView;
    }

    public void showTimePickerDialog() {
        if (mDateAndTimeCombine == null) {
            mDateAndTimeCombine = new DateAndTimeCombine(this);
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
        Snackbar.make(mView, getString(R.string.new_note_error_date), Snackbar.LENGTH_LONG).show();
        checkError = false;
        switchNotification.setChecked(false);
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
}
