package com.gawk.voicenotes.adapters.date_and_time;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by GAWK on 21.02.2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    DateAndTimeCombine mDateAndTimeCombine;
    Calendar current;

    public TimePickerFragment(DateAndTimeCombine mDateAndTimeCombine) {
        this.mDateAndTimeCombine = mDateAndTimeCombine;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int hour,minute;

        if (current == null) {
            current = Calendar.getInstance();
        }
        hour = current.get(Calendar.HOUR_OF_DAY);
        minute = current.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mDateAndTimeCombine.fail();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mDateAndTimeCombine.endSelectTime(hourOfDay, minute);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        Log.e("GAWK_ERR","TimePickerFragment() called");
    }
}
