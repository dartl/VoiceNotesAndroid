package com.gawk.voicenotes;

import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.gawk.voicenotes.preferences.PrefUtil;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by GAWK on 01.03.2017.
 */

public class NoteView extends ParentActivity {
    private TextView textViewDate, textViewBodyNotification;
    private EditText editTextNoteText;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_view);
        initAdMob(true);

        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewBodyNotification = (TextView) findViewById(R.id.textViewBodyNotification);
        editTextNoteText = (EditText) findViewById(R.id.editTextNoteText);

        setTextNote();
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdMob(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdView.destroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdView.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        actionSave.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean ret = super.onOptionsItemSelected(item);
        int id = item.getItemId();

        dbHelper.connection();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_note) {
            note.setText_note(editTextNoteText.getText().toString());
            dbHelper.saveNote(note,1);
            finish();
            return true;
        }

        return ret;
    }

    private void setTextNote() {
        dbHelper.connection();
        long id = getIntent().getLongExtra("id",-1);
        boolean ringtoneB = getIntent().getBooleanExtra("ringtone",false);
        PrefUtil prefUtil = new PrefUtil(this);
        String stringRingtone = prefUtil.getString(PrefUtil.NOTIFICATION_SOUND,"");
        if (ringtoneB) {
            RingtoneManager ringtoneManager = new RingtoneManager(this);
            ringtoneManager.getRingtone(ringtoneManager.getRingtonePosition(Uri.parse(stringRingtone))).stop();
        }
        if (id != -1) {
            note = new Note(dbHelper.getNoteById(id));
            editTextNoteText.setText(note.getText_note());

            DateFormat dateFormat;
            dateFormat = SimpleDateFormat.getDateTimeInstance();
            textViewDate.setText(dateFormat.format(note.getDate()));

            dbHelper.deleteAllOldNotification();
            Cursor cursorDate = dbHelper.getAllNotificationByNote(id);
            if (cursorDate.moveToFirst()) {
                Notification notification = new Notification(cursorDate);
                textViewBodyNotification.setText(dateFormat.format(notification.getDate()));
            }
        } else {
            finish();
        }
    }
}
