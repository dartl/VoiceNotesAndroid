package com.gawk.voicenotes.fragments_notes;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.listeners.TimePickerReturn;
import com.gawk.voicenotes.date_and_time.DateAndTimeCombine;
import com.gawk.voicenotes.logs.CustomLogger;
import com.gawk.voicenotes.models.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteNotifications extends FragmentParent implements TimePickerReturn {

    private Switch switchNotification;
    private RelativeLayout notificationLayout;
    private Button selectTime;
    private TextView textViewNowDate;
    private ToggleButton toggleButton_Sound, toggleButton_Shake;

    private Calendar dateNotification;
    private DateAndTimeCombine mDateAndTimeCombine;
    private boolean checkError, checkNotification = false;
    private View mView;

    private final Notification notification = new Notification();
    private final CustomLogger mCustomLogger = new CustomLogger();

    public NewNoteNotifications() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.new_note_notifications, null);

        switchNotification =  mView.findViewById(R.id.switchNotification);
        notificationLayout =  mView.findViewById(R.id.notificationLayout);
        selectTime = mView.findViewById(R.id.buttonSelectTime);
        textViewNowDate = mView.findViewById(R.id.textViewNowDate);
        toggleButton_Sound = mView.findViewById(R.id.toggleButton_Sound);
        toggleButton_Shake =  mView.findViewById(R.id.toggleButton_Shake);

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked && !checkError) {
                        showTimePickerDialog();
                    }
                    for (int i = 0; i < notificationLayout.getChildCount(); i++) {
                        notificationLayout.getChildAt(i).setEnabled(isChecked);
                    }
                    checkNotification = isChecked;
                } catch (Exception e) {
                    mCustomLogger.write(e);
                }

            }
        });

        toggleButton_Sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notification.setSound(isChecked);
            }
        });

        toggleButton_Shake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notification.setShake(isChecked);
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
}
