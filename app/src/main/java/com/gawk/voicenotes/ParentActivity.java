package com.gawk.voicenotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.gawk.voicenotes.adapters.NoteRecyclerAdapter;
import com.gawk.voicenotes.adapters.ParcelableUtil;
import com.gawk.voicenotes.adapters.PrefUtil;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.TimeNotification;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by GAWK on 06.02.2017.
 */

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int RC_BUY = 1001;
    public String SKU_SMALL_DONATE = "donation_one_dollor";
    public String SKU_BIG_DONATE = "big_donate";
    public final String INSTALL_PREF = "install_app";
    public final String DONATE_PREF = "donate_app";
    private Toolbar toolbar;
    protected MenuItem actionRemoveSelected, actionSave, actionSearch;
    private NavigationView navigationView;
    public SQLiteDBHelper dbHelper;
    protected AdView mAdView;
    FirebaseAnalytics mFirebaseAnalytics;
    IInAppBillingService mService;
    private ArrayList<JSONObject> allSubscriptionsInfo;
    private SharedPreferences sPref;
    private ActionBarDrawerToggle toggle;
    private Button buttonDonateDeveloper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = SQLiteDBHelper.getInstance(this);
        dbHelper.connection();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sPref = getPreferences(MODE_PRIVATE);

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        boolean bindResult = bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            Log.e("GAWK_ERR","CALL onServiceConnected()");
            mService = IInAppBillingService.Stub.asInterface(service);
            try {
                checkBuySubs();
                setAllSubscriptionsInfo(getAllSubscriptions());
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
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.menu_notes_list).getActionView();
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
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
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
        navigationView.setNavigationItemSelectedListener(this);

        buttonDonateDeveloper = (Button) findViewById(R.id.buttonDonateDeveloper);
        buttonDonateDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubscriptionActivity.class);
                startActivity(intent);
            }
        });
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
        actionRemoveSelected = menu.findItem(R.id.action_remove_selected);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remove_selected) {
            actionRemoveSelected();
            return true;
        }

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
        } else if (id == R.id.menu_help) {
            intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void actionRemoveSelected() {

    }

    public void initAdMob(boolean check) {
        int aBoolean = getsPref().getInt(DONATE_PREF,0);
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

    public void checkBuySubs() throws RemoteException {
        SharedPreferences.Editor ed = getsPref().edit();
        Bundle activeSubs = mService.getPurchases(3, getPackageName(),
                "subs", "");

        int response = activeSubs.getInt("RESPONSE_CODE");
        if (response == 0) {
            ArrayList<String> ownedSkus =
                    activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            if (ownedSkus.size() > 0) {
                ed.putInt(DONATE_PREF,2);
                ed.commit();
                initAdMob(false);
            } else {
                ed.putInt(DONATE_PREF,1);
            }
        }
    }

    public ArrayList<JSONObject> getAllSubscriptions() throws RemoteException {
        ArrayList<String> skuList = new ArrayList<> ();
        skuList.add(SKU_BIG_DONATE);
        skuList.add(SKU_SMALL_DONATE);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        if ( mService == null) {
            return null;
        }
        Bundle skuDetails = mService.getSkuDetails(3,
                getPackageName(), "subs", querySkus);
        int response = skuDetails.getInt("RESPONSE_CODE");
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        if (response == 0) {
            ArrayList<String> responseList
                    = skuDetails.getStringArrayList("DETAILS_LIST");

            for (String thisResponse : responseList) {
                JSONObject object;
                try {
                    object = new JSONObject(thisResponse);
                    jsonObjects.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("GAWK_ERR","getAllSubscriptions() called. jsonObjects = " + jsonObjects.toString());
        return jsonObjects;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && data !=null) {
            if (resultCode == RESULT_OK) {
                SharedPreferences.Editor ed = getsPref().edit();
                ed.putInt(DONATE_PREF,2);
                ed.apply();
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
        Log.e("GAWK_ERR","ParentActivity sound_link = " + sound_link);
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
                am.setExact(AlarmManager.RTC_WAKEUP,  notification.getDate().getTime() , pendingIntent);
            }
            else{
                am.set(AlarmManager.RTC_WAKEUP,  notification.getDate().getTime() , pendingIntent);
            }
        }
        else{
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
        Context context = ParentActivity.this;

        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(getResources().getText(R.string.vote_text_header));  // заголовок
        ad.setMessage(getResources().getText(R.string.vote_text_body)); // сообщение
        ad.setPositiveButton(getResources().getText(R.string.vote_text_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.gawk.voicenotes"));
                startActivity(i);
            }
        });
        ad.setNegativeButton(getResources().getText(R.string.vote_text_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.cancel();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.cancel();
            }
        });
        ad.show();
    }

    public SharedPreferences getsPref() {
        return sPref;
    }

    public void setsPref(SharedPreferences sPref) {
        this.sPref = sPref;
    }

    public ArrayList<JSONObject> getAllSubscriptionsInfo() {
        return allSubscriptionsInfo;
    }

    public void setAllSubscriptionsInfo(ArrayList<JSONObject> allSubscriptionsInfo) {
        this.allSubscriptionsInfo = allSubscriptionsInfo;
    }
}