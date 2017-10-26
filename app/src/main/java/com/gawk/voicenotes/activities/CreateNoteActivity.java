package com.gawk.voicenotes.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.listeners.TimePickerReturn;
import com.gawk.voicenotes.adapters.ViewPagerAdapter;
import com.gawk.voicenotes.activities.fragments.create_note.NewNoteNotifications;
import com.gawk.voicenotes.activities.fragments.create_note.NewNoteText;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;

import java.util.Calendar;

/**
 * Created by GAWK on 12.02.2017.
 */

public class CreateNoteActivity extends ParentActivity implements TimePickerReturn {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private NewNoteText newNoteText;

    /* объявляем все элементы активные */
    private Button newNoteAddNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        tab.setVisibility(View.VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager_new_note);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        createTabIcons();

        newNoteAddNotification =  (Button) findViewById(R.id.button_save_note);
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
    public void onResume() {
        super.onResume();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(R.id.menu_add).setCheckable(true).setChecked(true);
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
        View tabOne = LayoutInflater.from(this).inflate(R.layout.tab_header, null);
        TextView tabOneName = tabOne.findViewById(R.id.textViewTabTitle);
        tabOneName.setText(adapter.getPageTitle(0));
        ImageView tabOneIcon = tabOne.findViewById(R.id.imageViewTabIcon);
        tabOneIcon.setImageResource(R.drawable.ic_note_white_24dp);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        View tabTwo = LayoutInflater.from(this).inflate(R.layout.tab_header, null);
        TextView tabTwoName = tabTwo.findViewById(R.id.textViewTabTitle);
        tabTwoName.setText(adapter.getPageTitle(1));
        ImageView tabTwoIcon = tabTwo.findViewById(R.id.imageViewTabIcon);
        tabTwoIcon.setImageResource(R.drawable.ic_alarm_white_24dp);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }

    private void startSaveNote() {
        // сохранение заметки
        dbHelper.connection();
        NewNoteText newNoteText = (NewNoteText) adapter.getItem(0);
        Note newNote = new Note(-1,newNoteText.getTextNote(), Calendar.getInstance().getTime(),newNoteText.getSelectedCategoryId());
        Log.e("GAWK_ERR",newNote.toString());
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
