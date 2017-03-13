package com.gawk.voicenotes;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.ViewPagerAdapter;
import com.gawk.voicenotes.fragments_main.NotesListFragment;
import com.gawk.voicenotes.fragments_main.NotificationsListFragment;

public class MainActivity extends ParentActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initAdMob(true);

        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        tab.setVisibility(View.VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();
        dbHelper = SQLiteDBHelper.getInstance(this);
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

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NotesListFragment(this), getResources().getString(R.string.new_notes));
        adapter.addFragment(new NotificationsListFragment(), getResources().getString(R.string.new_note_notification));
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

    public void actionRemoveSelected() {
        int i = viewPager.getCurrentItem();
        switch (i) {
            case 0:
                NotesListFragment notesListFragment = (NotesListFragment) adapter.getItem(i);
                notesListFragment.showDialogDelete(-1,1);
                break;
            case 1:
                NotificationsListFragment notificationsListFragment = (NotificationsListFragment) adapter.getItem(i);
                notificationsListFragment.showDialogDelete(-1,1);
                break;
            default:
                break;
        }
        Log.d("GAWK_ERR","переопределилось");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        actionRemoveSelected.setVisible(true);
        return true;
    }

    public NotificationsListFragment getFragment(int position) {
        return (NotificationsListFragment) adapter.getItem(position);
    }

}
