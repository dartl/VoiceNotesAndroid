package com.gawk.voicenotes.fragments_notes;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.TimePickerReturn;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteNotifications extends Fragment implements TimePickerReturn {
    private Switch switchNotification;
    private RelativeLayout notificationLayout;
    private Button selectTime, selectDate;
    private TextView textViewNowDate;

    private Calendar dateNotification;
    private DialogFragment newFragmentDate, newFragmentTime;

    public NewNoteNotifications() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_note_notifications, null);

        switchNotification = (Switch) view.findViewById(R.id.switchNotification);
        notificationLayout = (RelativeLayout) view.findViewById(R.id.notificationLayout);
        selectTime = (Button) view.findViewById(R.id.buttonSelectTime);
        selectDate = (Button) view.findViewById(R.id.buttonSelectDate);
        textViewNowDate = (TextView) view.findViewById(R.id.textViewNowDate);

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i =0; i < notificationLayout.getChildCount(); i++) {
                    notificationLayout.getChildAt(i).setEnabled(isChecked);
                }
            }
        });

        dateNotification = Calendar.getInstance();
        setNotificationTime();
        return view;
    }

    public void showTimePickerDialog(View v) {
        if (newFragmentTime == null) {
            newFragmentTime = new TimePickerFragment();
        }
        newFragmentTime.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        if (newFragmentDate == null) {
            newFragmentDate = new DatePickerFragment();
        }
        newFragmentDate.show(getFragmentManager(), "datePicker");
    }

    private void setNotificationTime() {

        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        textViewNowDate.setText(dateFormat.format(dateNotification.getTime()));
    }

    @Override
    public void getTime(int hourOfDay, int minute) {
        dateNotification.set(dateNotification.get(Calendar.YEAR),
                dateNotification.get(Calendar.MONTH),
                dateNotification.get(Calendar.DAY_OF_MONTH),
                hourOfDay, minute, 0);
        setNotificationTime();
    }

    @Override
    public void getDate(int year, int month, int dayOfMonth) {
        dateNotification.set(year, month, dayOfMonth,
                dateNotification.get(Calendar.HOUR), dateNotification.get(Calendar.MINUTE), 0);
        setNotificationTime();
    }
}
