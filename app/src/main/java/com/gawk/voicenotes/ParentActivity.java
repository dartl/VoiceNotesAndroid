package com.gawk.voicenotes;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.gawk.voicenotes.adapters.NoteCursorAdapter;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by GAWK on 06.02.2017.
 */

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    private static final int BILLING_RESPONSE_RESULT_OK = 0;
    private static final int RC_BUY = 1001;
    private Toolbar toolbar;
    protected MenuItem actionRemoveSelected, actionSave;
    private NavigationView navigationView;
    public SQLiteDBHelper dbHelper;
    protected AdView mAdView;
    FirebaseAnalytics mFirebaseAnalytics;
    IInAppBillingService mService;

    String SKU_ONE_DOLLOR = "donation_one_dollor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = SQLiteDBHelper.getInstance(this);
        dbHelper.connection();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Intent intent = new Intent(this, IInAppBillingService.class);


        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        boolean bindResult = bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        Log.e("GAWK_ERR",String.valueOf(bindResult));

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
                startBuySubscription();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to IInAppBillingService

    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelper.connection();
        Cursor noteCursor = dbHelper.getCursorAllNotes();
        NoteCursorAdapter noteCursorAdapter = new NoteCursorAdapter(this, noteCursor, true);
        TextView view = (TextView) navigationView.getMenu().findItem(R.id.menu_notes_list).getActionView();
        view.setText(noteCursorAdapter.getCount() > 0 ? String.valueOf(noteCursorAdapter.getCount()) : null);
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        } else if (id == R.id.menu_sync) {

        } else if (id == R.id.menu_settings) {

        } else if (id == R.id.menu_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void actionRemoveSelected() {

    }

    public void initAdMob(boolean check) {
        mAdView = (AdView) findViewById(R.id.adView);
        if (check) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }
    }

    public void startBuySubscription() throws RemoteException {
        /*Bundle bundle = mService.getBuyIntent(3, getPackageName(),
                SKU_ONE_DOLLOR, "subs", "");

        PendingIntent pendingIntent = bundle.getParcelable(RESPONSE_BUY_INTENT);
        if (bundle.getInt("RESPONSE_CODE") == BILLING_RESPONSE_RESULT_OK) {
            // Start purchase flow (this brings up the Google Play UI).
            // Result will be delivered through onActivityResult().
            try {
                startIntentSenderForResult(pendingIntent.getIntentSender(), RC_BUY, new Intent(),
                        Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Log.e("GAWK_ERR","You have bought the " + sku);
                }
                catch (JSONException e) {
                    Log.e("GAWK_ERR","Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }
}