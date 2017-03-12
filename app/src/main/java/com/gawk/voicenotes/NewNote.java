package com.gawk.voicenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.TimePickerReturn;
import com.gawk.voicenotes.adapters.ViewPagerAdapter;
import com.gawk.voicenotes.fragments_notes.NewNoteNotifications;
import com.gawk.voicenotes.fragments_notes.NewNoteText;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_add));

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== getResources().getInteger(R.integer.constant_request_code_recognize) && resultCode==RESULT_OK) {
            newNoteText.setActivityResult(data);
        }
    }

    private void startSaveNote() {
        // сохранение заметки
        NewNoteText newNoteText = (NewNoteText) adapter.getItem(0);
        Note newNote = new Note(-1,newNoteText.getTextNote(), Calendar.getInstance().getTime());
        long note_id = dbHelper.saveNote(newNote, 0);

        // сохранение оповещения
        NewNoteNotifications newNoteNotifications = (NewNoteNotifications) adapter.getItem(1);
        Notification notification = newNoteNotifications.getNotification();
        if (notification != null) {
            notification.setId_note(note_id);
            dbHelper.saveNotification(notification,0);
        }
        finish();
    }

    @Override
    public void getTime(int hourOfDay, int minute) {
        TimePickerReturn parent = (TimePickerReturn) adapter.getItem(1);
        parent.getTime(hourOfDay,minute);
    }

    @Override
    public void getDate(int year, int month, int dayOfMonth) {
        TimePickerReturn parent = (TimePickerReturn) adapter.getItem(1);
        parent.getDate(year, month, dayOfMonth);
    }
}
