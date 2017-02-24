package com.gawk.voicenotes.adapters;

/**
 * Created by GAWK on 24.02.2017.
 */

public interface ActionsListNotes {
    public void showDialogDelete(final long id, final int state);
    public void selectNote(long id, boolean checked);
}
