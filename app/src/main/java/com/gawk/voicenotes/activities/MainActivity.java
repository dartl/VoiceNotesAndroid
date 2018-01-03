package com.gawk.voicenotes.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.CategoriesSpinner;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.ViewPagerAdapter;
import com.gawk.voicenotes.activities.fragments.main_activity.CategoryListFragment;
import com.gawk.voicenotes.activities.fragments.main_activity.NotesListFragment;
import com.gawk.voicenotes.activities.fragments.main_activity.NotificationsListFragment;
import com.gawk.voicenotes.adapters.preferences.PrefUtil;
import com.gawk.voicenotes.adapters.speech_recognition.LanguageDetailsChecker;

import java.util.Locale;

public class MainActivity extends ParentActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private SearchView searchView;
    private View mView;
    private boolean mShowNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.gawk.voicenotes.R.layout.activity_main);

        showInformationAboutPermissions();

        mView = findViewById(R.id.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1 || position == 2) {
                    actionSearchVisible(false);
                } else {
                    actionSearchVisible(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();
        dbHelper = SQLiteDBHelper.getInstance(this);
        verifyAllPermissions(this);

        if (mPrefUtil.getStringSet(PrefUtil.SUPPORTED_LANGUAGE_FOR_RECOGNIZE, null) == null) {
            Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
            sendOrderedBroadcast(
                    detailsIntent, null, new LanguageDetailsChecker(), null, Activity.RESULT_OK, null, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShowNotification = getIntent().getBooleanExtra("com.gawk.voicenotes.activities.mainactivity.shownotification", false);

        if (mShowNotification) {
            viewPager.setCurrentItem(1);
        }

        PrefUtil prefUtil = new PrefUtil(this);
        boolean boolInstall = prefUtil.getBoolean(INSTALL_PREF,false);
        if (!boolInstall) {
            if (dbHelper.getCountNotes() >= 2) {
                showVote();
                prefUtil.saveBoolean(INSTALL_PREF,true);
            }
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(R.id.menu_notes_list).setCheckable(true).setChecked(true);

        TextView textView = navigationView.getMenu().findItem(R.id.menu_notes_list).getActionView().findViewById(R.id.counterNotes);
        textView.setTextColor(getColorByAttr(R.attr.primaryColor));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        NotesListFragment notesListFragment = new NotesListFragment();
        notesListFragment.setMainActivity(this);
        adapter.addFragment(notesListFragment, getResources().getString(R.string.new_notes));

        NotificationsListFragment notificationsListFragment = new NotificationsListFragment();
        adapter.addFragment(notificationsListFragment, getResources().getString(R.string.new_note_notifications));

        CategoryListFragment categoryListFragment = new CategoryListFragment(notesListFragment);
        categoryListFragment.setMainActivity(this);
        adapter.addFragment(categoryListFragment, getResources().getString(R.string.main_view_categories_title));

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

        View tabThree = LayoutInflater.from(this).inflate(R.layout.tab_header, null);
        TextView tabThreeName = tabThree.findViewById(R.id.textViewTabTitle);
        tabThreeName.setText(adapter.getPageTitle(2));
        ImageView tabThreeIcon = tabThree.findViewById(R.id.imageViewTabIcon);
        tabThreeIcon.setImageResource(R.drawable.ic_collections_bookmark_white_24dp);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        actionSearchVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final NotesListFragment listFragment = (NotesListFragment) adapter.getItem(0);
        switch (id) {
            case R.id.action_search:
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
                break;
            case R.id.action_filter:
                listFragment.filter(true);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public NotificationsListFragment getFragment(int position) {
        return (NotificationsListFragment) adapter.getItem(position);
    }

    public boolean actionSearchVisible(boolean b) {
        if (actionSearch != null) {
            actionSearch.setVisible(b);
            actionFilter.setVisible(b);
        }
        return true;
    }

    public void filterNotes(long category_id) {
        viewPager.setCurrentItem(0);
        NotesListFragment notesListFragment = (NotesListFragment) adapter.getItem(0);
        notesListFragment.changeCategoryFilter(category_id);
        notesListFragment.filter(false);
    }

    void showInformationAboutPermissions() {

        if (mPrefUtil.getBoolean(PrefUtil.INFORMATION_ABOUT_PERMISSIONS,true)) {
            mPrefUtil.saveBoolean(PrefUtil.INFORMATION_ABOUT_PERMISSIONS,false);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.information_about_permissions_title)
                    .setMessage(getSpannedText(getString(R.string.information_about_permissions_text)))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
            alertDialog.show();
        }
    }

    private Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity активность в которой проверяем
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
                }
            }
        }
    }
}
