package com.gawk.voicenotes.adapters.date_and_time;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by GAWK on 23.02.2017.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    DateAndTimeCombine mDateAndTimeCombine;
    Calendar current = Calendar.getInstance();

    public DatePickerFragment() {}

    public DatePickerFragment(DateAndTimeCombine mDateAndTimeCombine) {
        this.mDateAndTimeCombine = mDateAndTimeCombine;
    }

    public DatePickerFragment(DateAndTimeCombine mDateAndTimeCombine, long time) {
        this.mDateAndTimeCombine = mDateAndTimeCombine;
        current.setTimeInMillis(time);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int year, month, dayOfMonth;

        year = current.get(Calendar.YEAR);
        month = current.get(Calendar.MONTH);
        dayOfMonth = current.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, dayOfMonth);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        current.set(year, month, dayOfMonth,
                current.get(Calendar.HOUR), current.get(Calendar.MINUTE), 0);
        mDateAndTimeCombine.endSelectData(year, month, dayOfMonth);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mDateAndTimeCombine.fail();
        Log.e("GAWK_ERR","DatePickerFragment onCancel");
    }

}

