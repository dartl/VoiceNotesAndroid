package com.gawk.voicenotes;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.listeners.TimePickerReturn;
import com.gawk.voicenotes.adapters.ViewPagerAdapter;
import com.gawk.voicenotes.fragments_notes.NewNoteNotifications;
import com.gawk.voicenotes.fragments_notes.NewNoteText;
import com.gawk.voicenotes.logs.CustomLogger;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

import java.util.Calendar;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNote extends ParentActivity implements TimePickerReturn {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private NewNoteText newNoteText;

    /* объявляем все элементы активные */
    private ImageButton newNoteAddNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);

        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        tab.setVisibility(View.VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager_new_note);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();

        newNoteAddNotification =  (ImageButton) findViewById(R.id.button_save_note);
        newNoteAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSaveNote();
            }
        });

        dbHelper = SQLiteDBHelper.getInstance(this);

        initAdMob(false);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        newNoteText = new NewNoteText();
        adapter.addFragment(newNoteText, getResources().getString(R.string.new_note));
        adapter.addFragment(new NewNoteNotifications(), getResources().getString(R.string.new_note_notification));
        viewPager.setAdapter(adapter);
    }

    private void createTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_header, null);
        tabOne.setText(adapter.getPageTitle(0));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_header, null);
        tabTwo.setText(adapter.getPageTitle(1));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_event_note_white_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }

    private void startSaveNote() {
        // сохранение заметки
        dbHelper.connection();
        NewNoteText newNoteText = (NewNoteText) adapter.getItem(0);
        Note newNote = new Note(-1,newNoteText.getTextNote(), Calendar.getInstance().getTime());
        long note_id = dbHelper.saveNote(newNote, 0);
        newNote.setId(note_id);

        // сохранение оповещения
        NewNoteNotifications newNoteNotifications = (NewNoteNotifications) adapter.getItem(1);
        if (newNoteNotifications.haveNotification()) {
            Notification notification = newNoteNotifications.getNotification();
            notification.setId_note(note_id);
            notification.setId(dbHelper.saveNotification(notification,0));
            restartNotify(newNote, notification);
        }
        dbHelper.disconnection();
        finish();
    }

    @Override
    public void setTimeAndDate(Calendar calendar) {
    }

    @Override
    public void fail() {
    }

}
