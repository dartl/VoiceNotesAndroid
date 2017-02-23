package com.gawk.voicenotes.adapters;

/**
 * Created by GAWK on 23.02.2017.
 */

public interface TimePickerReturn {
    public void getTime(int hourOfDay, int minute);
    public void getDate(int year, int month, int dayOfMonth);
}
