package com.gawk.voicenotes;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.adapters.TimePickerReturn;
import com.gawk.voicenotes.adapters.ViewPagerAdapter;
import com.gawk.voicenotes.fragments_notes.NewNoteNotifications;
import com.gawk.voicenotes.fragments_notes.NewNoteText;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNote extends ParentActivity implements TimePickerReturn {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    /* объявляем все элементы активные */
    private ImageButton newNoteAddNotification, newNoteAdd, newNoteClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);
        super.setToolbarTitlte("Новая заметка");

        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        tab.setVisibility(View.VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager_new_note);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewNoteText(), "Заметка");
        adapter.addFragment(new NewNoteNotifications(), "Оповещения");
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
