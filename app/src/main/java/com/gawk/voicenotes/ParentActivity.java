package com.gawk.voicenotes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.gawk.voicenotes.lists_adapters.NoteRecyclerAdapter;
import com.gawk.voicenotes.adapters.ParcelableUtil;
import com.gawk.voicenotes.listeners.SocialShare;
import com.gawk.voicenotes.logs.CustomLogger;
import com.gawk.voicenotes.preferences.PrefUtil;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.TimeNotification;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;
import com.gawk.voicenotes.subs.GooglePlaySubs;
import com.gawk.voicenotes.windows.VotesDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Created by GAWK on 06.02.2017.
 */

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public final String INSTALL_PREF = "install_app";
    private Toolbar toolbar;
    protected MenuItem actionSave, actionSearch;
    private NavigationView navigationView, navigationViewMenu;
    public SQLiteDBHelper dbHelper;
    protected AdView mAdView;
    FirebaseAnalytics mFirebaseAnalytics;
    IInAppBillingService mService;
    private ActionBarDrawerToggle toggle;
    private Button buttonDonateDeveloper;
    private ImageButton mButtonFacebook, mButtonVk, mButtonGoogle, mButtonTwitter, mButtonLinkedln,
            mButtonOdnoklassnik;
    private MenuItem mOldMenuItem;
    protected GooglePlaySubs mGooglePlaySubs;
    protected ParentActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefUtil prefUtil = new PrefUtil(this);

        /* Проверяем наличие настройки для интервала напоминания, если нет - добавляем стандартные 5 минут */
        if (prefUtil.getLong(prefUtil.NOTIFICATION_INTERVAL,-1) == -1) {
            prefUtil.saveLong(prefUtil.NOTIFICATION_INTERVAL,60000 * 5);
        }

        dbHelper = SQLiteDBHelper.getInstance(this);
        dbHelper.connection();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mActivity = this;

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            try {
                mGooglePlaySubs = new GooglePlaySubs(mActivity,mService);
                mGooglePlaySubs.checkBuySubs();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelper.connection();
        Cursor noteCursor = dbHelper.getCursorAllNotes();
        NoteRecyclerAdapter noteCursorAdapter = new NoteRecyclerAdapter(this, noteCursor);
        TextView view = (TextView) navigationViewMenu.getMenu().findItem(R.id.menu_notes_list).getActionView();
        view.setText(noteCursorAdapter.getItemCount() > 0 ? String.valueOf(noteCursorAdapter.getItemCount()) : null);
    }

    @Override
    public void onPause() {
        super.onPause();
        dbHelper.disconnection();
    }

    @Override
    public void onStop() {
        super.onStop();
        dbHelper.disconnection();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        TabLayout tab = (TabLayout) fullView.findViewById(R.id.tabs);
        tab.setVisibility(View.GONE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationViewMenu = (NavigationView) navigationView.findViewById(R.id.nav_view_menu);
        navigationViewMenu.setNavigationItemSelectedListener(this);

        buttonDonateDeveloper = (Button) findViewById(R.id.buttonDonateDeveloper);
        buttonDonateDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubscriptionActivity.class);
                startActivity(intent);
            }
        });

        SocialShare socialShare =  new SocialShare(this);

        mButtonFacebook = (ImageButton) fullView.findViewById(R.id.buttonFacebook);
        mButtonVk = (ImageButton) fullView.findViewById(R.id.buttonVk);
        mButtonGoogle = (ImageButton) fullView.findViewById(R.id.buttonGoogle);
        mButtonTwitter = (ImageButton) fullView.findViewById(R.id.buttonTwitter);
        mButtonLinkedln = (ImageButton) fullView.findViewById(R.id.buttonLinkedln);
        mButtonOdnoklassnik = (ImageButton) fullView.findViewById(R.id.buttonOdnoklassnik);

        mButtonFacebook.setOnClickListener(socialShare);
        mButtonVk.setOnClickListener(socialShare);
        mButtonGoogle.setOnClickListener(socialShare);
        mButtonTwitter.setOnClickListener(socialShare);
        mButtonLinkedln.setOnClickListener(socialShare);
        mButtonOdnoklassnik.setOnClickListener(socialShare);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        actionSave = menu.findItem(R.id.action_save_note);
        actionSearch = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.menu_add) {
            intent = new Intent(this, NewNote.class);
            startActivity(intent);
        } else if (id == R.id.menu_notes_list) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_import_export) {
            intent = new Intent(this, ExportImportActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_subs) {
            intent = new Intent(this, SubscriptionActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_statistics) {
            intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_help) {
            intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initAdMob(boolean check) {
        int aBoolean;
        if (mGooglePlaySubs != null) {
            aBoolean = mGooglePlaySubs.subsGetActive();
        } else {
            aBoolean = 0;
        }
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                buttonDonateDeveloper.setVisibility(View.GONE);
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                buttonDonateDeveloper.setVisibility(View.VISIBLE);
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                buttonDonateDeveloper.setVisibility(View.GONE);
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                buttonDonateDeveloper.setVisibility(View.VISIBLE);
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                buttonDonateDeveloper.setVisibility(View.VISIBLE);
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        if (check && (aBoolean != 2)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.bringToFront();
        } else {
            mAdView.setVisibility(View.GONE);
            buttonDonateDeveloper.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && data !=null) {
            if (resultCode == RESULT_OK) {
                mGooglePlaySubs.subsSetActive();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void restartNotify(Note note, Notification notification) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeNotification.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        PrefUtil prefUtil = new PrefUtil(this);
        String sound_link = prefUtil.getString(PrefUtil.NOTIFICATION_SOUND,"a");
        Bundle c = new Bundle();
        c.putString("sound_link", sound_link);
        c.putByteArray("note", ParcelableUtil.marshall(note));
        c.putByteArray("notification", ParcelableUtil.marshall(notification));
        intent.putExtras(c);
        int requestCodeIntent =  (int) notification.getId();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, -requestCodeIntent,
                intent, 0);
        // На случай, если мы ранее запускали активити, а потом поменяли время,
        // откажемся от уведомления
        am.cancel(pendingIntent);
        if(Build.VERSION.SDK_INT < 23){
            if(Build.VERSION.SDK_INT >= 19){
                Log.e("GAWK_ERR","Build.VERSION.SDK_INT >= 19");
                am.setExact(AlarmManager.RTC_WAKEUP,  notification.getDate().getTime() , pendingIntent);
            }
            else{
                Log.e("GAWK_ERR","NO Build.VERSION.SDK_INT >= 19");
                am.set(AlarmManager.RTC_WAKEUP,  notification.getDate().getTime() , pendingIntent);
            }
        }
        else{
            Log.e("GAWK_ERR","NO Build.VERSION.SDK_INT < 23");
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  notification.getDate().getTime() , pendingIntent);
        }
    }

    public void deleteNotify(long id) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeNotification.class);
        int requestCodeIntent =  (int) id;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, -requestCodeIntent,
                intent, 0);
        // На случай, если мы ранее запускали активити, а потом поменяли время,
        // откажемся от уведомления
        am.cancel(pendingIntent);
    }

    protected void installIcon() {
        //where this is a context (e.g. your current activity)
        final Intent shortcutIntent = new Intent(this, MainActivity.class);

        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        // Sets the custom shortcut's title
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        // Set the custom shortcut icon
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.icon175x175_big));
        // add the shortcut
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(intent);
    }

    protected void showVote() {
        VotesDialog votesDialog = new VotesDialog();
        votesDialog.show(getFragmentManager(),"VotesDialog");
    }

    // Storage Permissions
    protected static final int REQUEST_PERMISSIONS = 13;
    protected static String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO
    };

    public static boolean checkPermissions(Activity activity, int i) {
        if (ActivityCompat.checkSelfPermission(activity, PERMISSIONS[i]) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static boolean checkAllPermissions(Activity activity) {
        for (int i= 0; i < PERMISSIONS.length;i++) {
            if (ActivityCompat.checkSelfPermission(activity, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}