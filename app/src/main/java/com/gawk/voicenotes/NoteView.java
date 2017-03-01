package com.gawk.voicenotes;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GAWK on 01.03.2017.
 */

public class NoteView extends ParentActivity {
    private TextView textViewDate, textViewTitleNotification, textViewBodyNotification;
    private EditText editTextNoteText;
    private SQLiteDBHelper sqLiteDBHelper;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_view);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name_note));

        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewTitleNotification = (TextView) findViewById(R.id.textViewTitleNotification);
        textViewBodyNotification = (TextView) findViewById(R.id.textViewBodyNotification);
        editTextNoteText = (EditText) findViewById(R.id.editTextNoteText);

        long id = getIntent().getLongExtra("id",-1);
        if (id != -1) {
            sqLiteDBHelper = SQLiteDBHelper.getInstance(this);
            note = new Note(sqLiteDBHelper.getNoteById(id));
            editTextNoteText.setText(note.getText_note());

            DateFormat dateFormat;
            dateFormat = SimpleDateFormat.getDateTimeInstance();
            textViewDate.setText(dateFormat.format(note.getDate()));

            Cursor cursorDate = sqLiteDBHelper.getAllNotificationByNote(id);
            if (cursorDate.moveToFirst()) {
                Notification notification = new Notification(cursorDate);
                textViewBodyNotification.setText(dateFormat.format(notification.getDate()));
            }
        } else {
            finish();
        }
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_note) {
            note.setText_note(editTextNoteText.getText().toString());
            sqLiteDBHelper.saveNote(note,1);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
