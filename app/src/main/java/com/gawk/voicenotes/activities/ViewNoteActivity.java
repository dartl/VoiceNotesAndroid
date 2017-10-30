package com.gawk.voicenotes.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.main_activity.NotificationsListFragment;
import com.gawk.voicenotes.adapters.ViewPagerAdapter;
import com.gawk.voicenotes.activities.fragments.view_note.NoteViewFragment;
import com.gawk.voicenotes.adapters.custom_layouts.CustomRelativeLayout;

/**
 * Created by GAWK on 01.03.2017.
 */

public class ViewNoteActivity extends ParentActivity implements CustomRelativeLayout.Listener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private CustomRelativeLayout mCustomRelativeLayout;

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnote);
        initAdMob(true);

        id = getIntent().getLongExtra("id",-1);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.viewpager_view_note);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();

        mCustomRelativeLayout = (CustomRelativeLayout) findViewById(R.id.customRelativeLayoutMain);
        mCustomRelativeLayout.setListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        NoteViewFragment noteViewFragment = new NoteViewFragment(id);
        mViewPagerAdapter.addFragment(noteViewFragment, getResources().getString(R.string.app_name_note));

        NotificationsListFragment notificationsListFragment = new NotificationsListFragment(id);
        mViewPagerAdapter.addFragment(notificationsListFragment, getResources().getString(R.string.new_note_notifications));

        viewPager.setAdapter(mViewPagerAdapter);
    }

    private void createTabIcons() {
        View tabOne = LayoutInflater.from(this).inflate(R.layout.tab_header, null);
        TextView tabOneName = tabOne.findViewById(R.id.textViewTabTitle);
        tabOneName.setText(mViewPagerAdapter.getPageTitle(0));
        ImageView tabOneIcon = tabOne.findViewById(R.id.imageViewTabIcon);
        tabOneIcon.setImageResource(R.drawable.ic_note_white_24dp);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        View tabTwo = LayoutInflater.from(this).inflate(R.layout.tab_header, null);
        TextView tabTwoName = tabTwo.findViewById(R.id.textViewTabTitle);
        tabTwoName.setText(mViewPagerAdapter.getPageTitle(1));
        ImageView tabTwoIcon = tabTwo.findViewById(R.id.imageViewTabIcon);
        tabTwoIcon.setImageResource(R.drawable.ic_alarm_white_24dp);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
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
            NoteViewFragment noteViewFragment = (NoteViewFragment) mViewPagerAdapter.getItem(0);
            dbHelper.saveNote(noteViewFragment.getUpdateNote(),1);
            finish();
            return true;
        }

        return ret;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSoftKeyboardShown(boolean isShowing) {
        changeAdMob(isShowing);
        if (viewPager.getCurrentItem() == 0) {
            CustomRelativeLayout.Listener noteViewFragment = (CustomRelativeLayout.Listener) mViewPagerAdapter.getItem(viewPager.getCurrentItem());
            noteViewFragment.onSoftKeyboardShown(isShowing);
        }
    }
}
