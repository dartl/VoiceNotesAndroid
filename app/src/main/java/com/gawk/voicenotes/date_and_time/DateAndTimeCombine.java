package com.gawk.voicenotes.date_and_time;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.gawk.voicenotes.listeners.TimePickerReturn;

import java.util.Calendar;

/**
 * Created by GAWK on 24.08.2017.
 */

public class DateAndTimeCombine {
    private final long MAX_DIFF_TIME = 60000;
    private DialogFragment mDatePickerFragment, mTimePickerFragment;
    private Calendar mCalendar;
    private FragmentManager mFragmentManager;
    private TimePickerReturn mTimePickerReturn;

    public DateAndTimeCombine(TimePickerReturn timePickerReturn) {
        this.mDatePickerFragment = new DatePickerFragment(this);
        this.mTimePickerFragment = new TimePickerFragment(this);
        this.mTimePickerReturn = timePickerReturn;
    }

    public void show(FragmentManager fragmentManager) {
        mCalendar = Calendar.getInstance();
        mFragmentManager = fragmentManager;
        mDatePickerFragment.show(mFragmentManager, "datePicker");
    }

    public void fail() {
        mTimePickerReturn.fail();
    }

    public void endSelectData(int year,int month,int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth,
                mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.MINUTE), 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        if (mCalendar.before(calendar)) {
            mTimePickerReturn.fail();
        } else {
            mTimePickerFragment.show(mFragmentManager, "timePicker");
        }
    }

    public void endSelectTime(int hourOfDay,int minute) {
        mCalendar.set(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                hourOfDay, minute, 0);
        if ( (mCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) > MAX_DIFF_TIME ) {
            mTimePickerReturn.setTimeAndDate(mCalendar);
        } else {
            mTimePickerReturn.fail();
        }
    }
}
