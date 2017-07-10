package com.gawk.voicenotes;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    private SearchView searchView;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initAdMob(true);

        mView = (View) findViewById(R.id.activity_main);


        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        tab.setVisibility(View.VISIBLE);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    actionSearchVisible(false);
                } else {
                    actionSearchVisible(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();
        dbHelper = SQLiteDBHelper.getInstance(this);
        verifyAllPermissions(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdMob(true);
        boolean boolInstall = getsPref().getBoolean(INSTALL_PREF,false);
        if (!boolInstall) {
            if (dbHelper.getCountNotes() >= 2) {
                installIcon();
                showVote();
                SharedPreferences.Editor ed = getsPref().edit();
                ed.putBoolean(INSTALL_PREF,true);
                ed.apply();
            }
        }
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
        NotesListFragment notesListFragment = new NotesListFragment();
        notesListFragment.setMainActivity(this);
        adapter.addFragment(notesListFragment, getResources().getString(R.string.new_notes));
        NotificationsListFragment notificationsListFragment = new NotificationsListFragment();
        notificationsListFragment.setMainActivity(this);
        adapter.addFragment(notificationsListFragment, getResources().getString(R.string.new_note_notification));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        actionRemoveSelected.setVisible(true);
        actionSearchVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            final FragmentParent listFragment = (FragmentParent) adapter.getItem(viewPager.getCurrentItem());

            searchView =
                    (SearchView) MenuItemCompat.getActionView(actionSearch);
            searchView.setQueryHint(getResources().getString(R.string.action_search) + "...");
            searchView.setOnQueryTextListener(
                    new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            listFragment.search(newText);
                            return true;
                        }
                    }
            );
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public NotificationsListFragment getFragment(int position) {
        return (NotificationsListFragment) adapter.getItem(position);
    }

    public boolean actionSearchVisible(boolean b) {
        if (actionSearch != null) {
            actionSearch.setVisible(b);
        }
        return true;
    }


    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyAllPermissions(Activity activity) {
        // Check if we have write permission
        for (int i= 0; i < PERMISSIONS.length;i++) {
            if (ActivityCompat.checkSelfPermission(activity, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS,
                        REQUEST_PERMISSIONS
                );
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                boolean mCheck = true;
                // If request is cancelled, the result arrays are empty.
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        mCheck = false;
                    }
                }
                if (mCheck) {
                    Snackbar.make(mView, getString(R.string.success), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mView, getString(R.string.main_permissions_error), Snackbar.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
