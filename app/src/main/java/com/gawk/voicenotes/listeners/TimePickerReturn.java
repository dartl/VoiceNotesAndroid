package com.gawk.voicenotes.listeners;

import java.util.Calendar;

/**
 * Created by GAWK on 23.02.2017.
 */

public interface TimePickerReturn {
    public void setTimeAndDate(Calendar calendar);
    public void fail();
}
