package com.gawk.voicenotes.activities.intents;

import android.app.Activity;
import android.os.Bundle;

import com.gawk.voicenotes.adapters.NotificationAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;
import com.google.android.gms.actions.NoteIntents;

import java.util.Date;

/**
 * Created by GAWK on 17.11.2017.
 */

public class IntentCreateNote extends Activity {
    public static String EXTRA_DATE_TIME = "com.gawk.voicenotes.createnote.timenotification";

    NotificationAdapter mNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotificationAdapter = new NotificationAdapter(this);

        String name = getIntent().getStringExtra(NoteIntents.EXTRA_NAME);
        String text = getIntent().getStringExtra(NoteIntents.EXTRA_TEXT);
        long time = getIntent().getLongExtra(EXTRA_DATE_TIME,0);

        if (text == null) text = "";

        if (name != null) {
            text = name + "\n" + text;
        }

        startSaveNote(text, time);

        finish();
    }

    private void startSaveNote(String text, long time) {
        SQLiteDBHelper dbHelper = SQLiteDBHelper.getInstance(this);
        // сохранение заметки
        dbHelper.connection();

        Note newNote = new Note();
        newNote.setText_note(text);
        long note_id = dbHelper.saveNote(newNote);
        newNote.setId(note_id);

        if (time > 0) {
            Notification notification = new Notification();
            notification.setDate(new Date(time));
            notification.setId_note(newNote.getId());

            notification.setId(dbHelper.saveNotification(notification));

            mNotificationAdapter.restartNotify(newNote,notification);
        }

        dbHelper.disconnection();
    }
}
