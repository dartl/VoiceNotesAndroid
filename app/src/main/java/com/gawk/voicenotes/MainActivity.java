package com.gawk.voicenotes;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gawk.voicenotes.adapters.ViewPagerAdapter;
import com.gawk.voicenotes.fragments_main.NotesListFragment;
import com.gawk.voicenotes.fragments_main.NotificationsListFragment;

public class MainActivity extends ParentActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        tab.setVisibility(View.VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NotesListFragment(), "Заметки");
        adapter.addFragment(new NotificationsListFragment(), "Оповещения");
        viewPager.setAdapter(adapter);
    }
}
