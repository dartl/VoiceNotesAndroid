package com.gawk.voicenotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gawk.voicenotes.adapters.ActionMenuBottom;
import com.gawk.voicenotes.adapters.ActionSpeechRecognition;
import com.gawk.voicenotes.adapters.NotificationAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.lists_adapters.ListAdapters;
import com.gawk.voicenotes.lists_adapters.NotificationRecyclerAdapter;
import com.gawk.voicenotes.models.Notification;
import com.gawk.voicenotes.preferences.PrefUtil;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.windows.SetNotification;
import com.gawk.voicenotes.windows.SpeechRecognitionDialog;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by GAWK on 01.03.2017.
 */

public class NoteView extends ParentActivity implements ActionMenuBottom, RecognitionListener {
    private TextView textViewDate;
    private NoteView mThis;
    private EditText editTextNoteText;
    private Note note;
    private ListAdapters mListAdapters;
    private RecyclerView mRecyclerView;
    private NotificationRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private View mView;
    private ScrollView mScrollViewReminders;
    private long id;
    private Button mButtonAddReminder;
    private NotificationAdapter mNotificationAdapter;
    private FloatingActionButton imageButton_NewNoteAdd, imageButton_NewNoteClear;
    private ActionSpeechRecognition mActionSpeechRecognition;
    private SpeechRecognitionDialog mSpeechRecognitionDialog;
    private boolean mCheckPartialResults = false;
    private String mPartialResultsStart;
    private SetNotification mSetNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_view);
        mThis = this;
        dbHelper.connection();

        mView = findViewById(R.id.view_note);

        initAdMob(true);

        textViewDate = (TextView) findViewById(R.id.textViewDate);
        editTextNoteText = (EditText) findViewById(R.id.editTextNoteText);
        mScrollViewReminders = (ScrollView) findViewById(R.id.scrollViewReminders);
        mButtonAddReminder = (Button) findViewById(R.id.buttonAddReminder);
        mButtonAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetNotification = new SetNotification(note.getId());
                mSetNotification.setNoteView(mThis);
                mSetNotification.show(getSupportFragmentManager(),"setNotification");
            }
        });

        imageButton_NewNoteAdd = (FloatingActionButton) findViewById(R.id.imageButton_NewNoteAdd);
        imageButton_NewNoteClear =  (FloatingActionButton) findViewById(R.id.imageButton_NewNoteClear);

        imageButton_NewNoteClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextNoteText.setText("");
            }
        });

        imageButton_NewNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecognizeDialog();
            }
        });

        mRecyclerView = mView.findViewById(R.id.listViewAllNotifications);

        dbHelper = SQLiteDBHelper.getInstance(this);
        dbHelper.connection();

        setTextNote();
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdMob(true);
        updateList();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mActionSpeechRecognition != null) {
            mActionSpeechRecognition.destroy();
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setTextNote() {
        id = getIntent().getLongExtra("id",-1);

        boolean ringtoneB = getIntent().getBooleanExtra("ringtone",false);
        PrefUtil prefUtil = new PrefUtil(this);
        String stringRingtone = prefUtil.getString(PrefUtil.NOTIFICATION_SOUND,"");
        if (ringtoneB) {
            RingtoneManager ringtoneManager = new RingtoneManager(this);
            ringtoneManager.getRingtone(ringtoneManager.getRingtonePosition(Uri.parse(stringRingtone))).stop();
        }

        Log.e("GAWK_ERR","id note = " + id);
        note = new Note(dbHelper.getNoteById(id));
        editTextNoteText.setText(note.getText_note());


        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance();
        textViewDate.setText(dateFormat.format(note.getDate()));

        Cursor notificationCursor =  dbHelper.getAllNotificationByNote(id);

        mListAdapters = new ListAdapters(mView,this,this);
        mListAdapters.changeVisibleItemMenu(R.id.action_share_element,false);
        mListAdapters.changeVisibleItemSelectedMenu(R.id.imageButtonShare, View.GONE);

        /* new NoteRecycler */
        mAdapter = new NotificationRecyclerAdapter(this, notificationCursor, mListAdapters, dbHelper);
        mAdapter.setViewNote(true);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = mView.findViewById(R.id.listViewAllNotifications);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mNotificationAdapter = new NotificationAdapter(this);
        long id_notification = getIntent().getLongExtra("id_notification",-1);
        if (id_notification != -1) {
            dbHelper.deleteNotification(id);
            mNotificationAdapter.removeNotify(id_notification);
            updateList();
        }

        updateList();

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            mScrollViewReminders.setVisibility(View.GONE);
                        } else {
                            mScrollViewReminders.setVisibility(View.VISIBLE);
                        }
                        updateList();
                    }
                });
    }

    private void showRecognizeDialog() {
        mSpeechRecognitionDialog = new SpeechRecognitionDialog();
        mActionSpeechRecognition = new ActionSpeechRecognition(this,this,mSpeechRecognitionDialog,this);
        mSpeechRecognitionDialog.setFragmentParent(mActionSpeechRecognition);
        mSpeechRecognitionDialog.show(getFragmentManager(),"SpeechRecognitionDialog");
    }

    public void saveNotification(Notification notification) {
        notification.setId(dbHelper.saveNotification(notification,0));
        NotificationAdapter notificationAdapter = new NotificationAdapter(this);
        notificationAdapter.restartNotify(note, notification);
        updateList();
    }

    public void failSetNotification() {
        Snackbar.make(mView, getString(R.string.new_note_error_date), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void updateList() {
        Cursor notificationCursor = dbHelper.getAllNotificationByNote(id);
        mAdapter.changeCursor(notificationCursor);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteItemList(long id, boolean stateRemoveAllNotification, ArrayList selectItems) {
        dbHelper.deleteNotification(id);
        updateList();
    }

    @Override
    public void shareItemList(long id, ArrayList selectItems) {

    }

    @Override
    public void refreshSelectedList() {

    }

    /* Recognition Speech */

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.e("GAWK_ERR","onReadyForSpeech()");
        mSpeechRecognitionDialog.setActive();
        mCheckPartialResults = false;
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {
        if (mSpeechRecognitionDialog != null) {
            mSpeechRecognitionDialog.changeVoiceValue((int) (mActionSpeechRecognition.convertDpToPixel((v*6),this)+
                    getResources().getDimension(R.dimen.dialog_recognize_circle_min_size)));
        }
    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {
        mActionSpeechRecognition.endRecognition();
        mSpeechRecognitionDialog.setInactive();
        mSpeechRecognitionDialog.errorMessage(i);
    }

    @Override
    public void onResults(Bundle results) {
        Log.e("GAWK_ERR","onResults(Bundle results)");
        ArrayList<String> thingsYouSaid = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (thingsYouSaid != null) {
            if (mPartialResultsStart.length() == 0) {
                editTextNoteText.setText(thingsYouSaid.get(0));
            } else {
                editTextNoteText.setText(mPartialResultsStart + thingsYouSaid.get(0));
            }
            mSpeechRecognitionDialog.dismiss();
        }
        mActionSpeechRecognition.endRecognition();
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.e("GAWK_ERR","onPartialResults(Bundle partialResults)");
        if (!mCheckPartialResults) {
            mPartialResultsStart = editTextNoteText.getText().toString();
            mCheckPartialResults = true;
            if (editTextNoteText.length() > 0) {
                mPartialResultsStart += " ";
            }
        }
        ArrayList<String> thingsYouSaid = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        editTextNoteText.setText(mPartialResultsStart+thingsYouSaid.get(0));
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

}
