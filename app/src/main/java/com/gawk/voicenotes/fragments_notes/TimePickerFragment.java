package com.gawk.voicenotes.fragments_notes;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.gawk.voicenotes.adapters.TimePickerReturn;

import java.util.Calendar;

/**
 * Created by GAWK on 21.02.2017.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TimePickerReturn parent;
    Calendar current;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int hour,minute;

        if (current == null) {
            current = Calendar.getInstance();
        }
        hour = current.get(Calendar.HOUR_OF_DAY);
        minute = current.get(Calendar.MINUTE);

        parent = (TimePickerReturn) getActivity();
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        current.set(current.get(Calendar.YEAR),
                current.get(Calendar.MONTH),
                current.get(Calendar.DAY_OF_MONTH),
                hourOfDay, minute, 0);
        parent.getTime(hourOfDay,minute);
    }
}
