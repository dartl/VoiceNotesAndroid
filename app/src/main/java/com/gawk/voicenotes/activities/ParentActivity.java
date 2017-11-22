package com.gawk.voicenotes.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.ColorInt;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.BannerView;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.NotificationAdapter;
import com.gawk.voicenotes.adapters.lists_adapters.NoteRecyclerAdapter;
import com.gawk.voicenotes.adapters.listeners.SocialShare;
import com.gawk.voicenotes.models.Statistics;
import com.gawk.voicenotes.adapters.preferences.PrefUtil;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.models.Notification;
import com.gawk.voicenotes.adapters.subs.GooglePlaySubs;
import com.gawk.voicenotes.windows.VotesDialog;
import com.yandex.metrica.YandexMetrica;

import java.lang.reflect.Method;


/**
 * Created by GAWK on 06.02.2017.
 */

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public final String API_KEY_ADS = "1a2a7d16840f9909fc9a1b747c0920118444cd6c54cd2a14";
    public final String API_KEY = "d766dee6-1292-4981-8845-966d5c4fd00c";
    public final String INSTALL_PREF = "install_app";
    protected MenuItem actionSave, actionSearch, actionFilter;
    private NavigationView navigationView, navigationViewMenu;
    public SQLiteDBHelper dbHelper;
    IInAppBillingService mService;
    private ActionBarDrawerToggle toggle;
    private Button buttonDonateDeveloper;
    private ImageButton mButtonFacebook, mButtonVk, mButtonGoogle, mButtonTwitter, mButtonLinkedln,
            mButtonOdnoklassnik;
    private ImageView mImageViewLevelIcon;
    private TextView mTextViewLevelRank, mTextViewLevelLevel, mTextViewLevelExperience;
    private NotificationAdapter mNotificationAdapter;
    protected GooglePlaySubs mGooglePlaySubs;
    protected ParentActivity mActivity;
    PrefUtil mPrefUtil;
    boolean mShowAdsAndDonate = true;
    boolean mIsShowKeyboard = false;
    BannerView mBannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        mPrefUtil = new PrefUtil(this);
        if (mPrefUtil.getInt(PrefUtil.THEME,-1) != -1) {
            setTheme(mPrefUtil.getInt(PrefUtil.THEME,-1));
        }
        super.onCreate(savedInstanceState);
        initAdMob();

        // Инициализация AppMetrica SDK
        YandexMetrica.activate(getApplicationContext(), API_KEY);
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(getApplication());

        mNotificationAdapter = new NotificationAdapter(this);

        /* Проверяем наличие настройки для интервала напоминания, если нет - добавляем стандартные 5 минут */
        if (mPrefUtil.getLong(PrefUtil.NOTIFICATION_INTERVAL,-1) == -1) {
            mPrefUtil.saveLong(PrefUtil.NOTIFICATION_INTERVAL,60000 * 5);
        }

        dbHelper = SQLiteDBHelper.getInstance(this);
        dbHelper.connection();


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
                //changeAdMob();
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
        int newTheme = mPrefUtil.getInt(PrefUtil.THEME,-1);
        if (newTheme != -1 && newTheme != getThemeId()) {
            recreate();
        }
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        dbHelper.connection();
        Cursor noteCursor = dbHelper.getCursorAllNotes();
        NoteRecyclerAdapter noteCursorAdapter = new NoteRecyclerAdapter(this, noteCursor);
        TextView view = (TextView) navigationViewMenu.getMenu().findItem(R.id.menu_notes_list).getActionView();
        view.setText(noteCursorAdapter.getItemCount() > 0 ? String.valueOf(noteCursorAdapter.getItemCount()) : null);
        refreshNavHeader();

        if (mShowAdsAndDonate) {
            Appodeal.onResume(mActivity, Appodeal.BANNER);
        }
        changeDonation();
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
        unbindService(mServiceConn);
    }

    @Override
    public void setContentView(int layoutResID) {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        TabLayout tab = fullView.findViewById(R.id.tabs);
        tab.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View view) {
                if (mShowAdsAndDonate) {
                    Appodeal.onResume(mActivity, Appodeal.BANNER_BOTTOM);
                    Appodeal.show(mActivity, Appodeal.BANNER_BOTTOM);
                }
                Log.e("GAWK_ERR","onDrawerClosed(View view)");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (mShowAdsAndDonate) {
                    Appodeal.hide(mActivity, Appodeal.BANNER_BOTTOM);;
                }

                Log.e("GAWK_ERR","onDrawerOpened(View drawerView)");
            };

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationViewMenu = navigationView.findViewById(R.id.nav_view_menu);
        navigationViewMenu.setNavigationItemSelectedListener(this);
        mBannerView = (BannerView) findViewById(R.id.appodealBannerView);
        buttonDonateDeveloper = (Button) findViewById(R.id.buttonDonateDeveloper);
        buttonDonateDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubscriptionActivity.class);
                startActivity(intent);
            }
        });

        SocialShare socialShare =  new SocialShare(this);

        mButtonFacebook = fullView.findViewById(R.id.buttonFacebook);
        mButtonVk = fullView.findViewById(R.id.buttonVk);
        mButtonGoogle = fullView.findViewById(R.id.buttonGoogle);
        mButtonTwitter = fullView.findViewById(R.id.buttonTwitter);
        mButtonLinkedln = fullView.findViewById(R.id.buttonLinkedln);
        mButtonOdnoklassnik = fullView.findViewById(R.id.buttonOdnoklassnik);

        mButtonFacebook.setOnClickListener(socialShare);
        mButtonVk.setOnClickListener(socialShare);
        mButtonGoogle.setOnClickListener(socialShare);
        mButtonTwitter.setOnClickListener(socialShare);
        mButtonLinkedln.setOnClickListener(socialShare);
        mButtonOdnoklassnik.setOnClickListener(socialShare);

        mImageViewLevelIcon = navigationView.findViewById(R.id.imageViewLevelIcon);
        mTextViewLevelRank = navigationView.findViewById(R.id.textViewLevelRank);
        mTextViewLevelLevel = navigationView.findViewById(R.id.textViewLevelLevel);
        mTextViewLevelExperience = navigationView.findViewById(R.id.textViewLevelExperience);
        refreshNavHeader();
    }

    public void refreshNavHeader() {
        Statistics statistics = new Statistics(this);
        if (statistics.getLevel() >= 10) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level2_white);
            mImageViewLevelIcon.setImageBitmap(bm);
            mTextViewLevelRank.setText(getText(R.string.statistics_level2));
        }
        if (statistics.getLevel() >= 30) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level3_white);
            mImageViewLevelIcon.setImageBitmap(bm);
            mTextViewLevelRank.setText(getText(R.string.statistics_level3));
        }
        if (statistics.getLevel() >= 50) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level4_white);
            mImageViewLevelIcon.setImageBitmap(bm);
            mTextViewLevelRank.setText(getText(R.string.statistics_level4));
        }
        if (statistics.getLevel() >= 100) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level5_white);
            mImageViewLevelIcon.setImageBitmap(bm);
            mTextViewLevelRank.setText(getText(R.string.statistics_level5));
        }
        if (statistics.getLevel() >= 500) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level6_white);
            mImageViewLevelIcon.setImageBitmap(bm);
            mTextViewLevelRank.setText(getText(R.string.statistics_level6));
        }
        mTextViewLevelLevel.setText(getText(R.string.statistics_level_title) + " " + statistics.getLevel());
        mTextViewLevelExperience.setText(String.valueOf(statistics.getExperience()) + "/" +
                String.valueOf(statistics.getBorderExperience() + statistics.getUpExperience()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            if (mShowAdsAndDonate) {
                Appodeal.show(mActivity, Appodeal.BANNER_BOTTOM);
            }
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
        actionFilter = menu.findItem(R.id.action_filter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.menu_add) {
            intent = new Intent(this, CreateNoteActivity.class);
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
        if (mShowAdsAndDonate) {
            Appodeal.show(mActivity, Appodeal.BANNER_BOTTOM);
        }
        return true;
    }

    public boolean checkSubs() {
        if (mGooglePlaySubs != null) {
            return mGooglePlaySubs.subsGetActive() == 2;
        }
        return false;
    }

    public void initAdMob() {
        if (!mShowAdsAndDonate) {
            Log.e("GAWK_ERR", "!mShowAdsAndDonate");
            try {
                buttonDonateDeveloper.setVisibility(View.GONE);
                Appodeal.hide(this, Appodeal.BANNER);
            } catch (NullPointerException e) {
                Log.e("GAWK_ERR", "initAdMob NullPointerException");
            }

            return;
        }
        if (!checkSubs()) {
            Appodeal.setBannerCallbacks(new BannerCallbacks() {
                @Override
                public void onBannerLoaded(int height, boolean isPrecache) {
                    Log.d("Appodeal", "onBannerLoaded");
                    mBannerView.getLayoutParams().height = height;
                    buttonDonateDeveloper.setVisibility(View.GONE);
                    //buttonDonateDeveloper.getLayoutParams().height = height;
                }
                @Override
                public void onBannerFailedToLoad() {
                    Log.d("Appodeal", "onBannerFailedToLoad");
                }
                @Override
                public void onBannerShown() {
                    mBannerView.setVisibility(View.VISIBLE);
                    Log.d("Appodeal", "onBannerShown");
                }
                @Override
                public void onBannerClicked() {
                    Log.d("Appodeal", "onBannerClicked");
                }
            });
            if (mShowAdsAndDonate) {
                Appodeal.disableNetwork(mActivity, "yandex");
                Appodeal.disableNetwork(mActivity, "adcolony");
                Appodeal.disableNetwork(mActivity, "chartboost");
                Appodeal.disableNetwork(mActivity, "ironsource");
                Appodeal.disableNetwork(mActivity, "startapp");
                Appodeal.disableNetwork(mActivity, "tapjoy");
                Appodeal.disableNetwork(mActivity, "unity_ads");
                Appodeal.disableNetwork(mActivity, "vungle");
                Appodeal.initialize(mActivity, API_KEY_ADS, Appodeal.BANNER);
                Appodeal.show(mActivity, Appodeal.BANNER_BOTTOM);
            }
        }
    }

    public void changeDonation() {
        mPrefUtil.saveInt(PrefUtil.DONATE_SHOW, mPrefUtil.getInt(PrefUtil.DONATE_SHOW,0) + 1);
        if (mShowAdsAndDonate && mPrefUtil.getInt(PrefUtil.DONATE_SHOW,0) >= 6) {
            buttonDonateDeveloper.setVisibility(View.VISIBLE);
            mPrefUtil.saveInt(PrefUtil.DONATE_SHOW, 0);
        } else {
            buttonDonateDeveloper.setVisibility(View.GONE);
        }
    }

    public void changeAdMob() {
        /*
        if (!Appodeal.isLoaded(Appodeal.BANNER_BOTTOM)) {
            Log.e("GAWK_ERR", "changeAdMob() called, !Appodeal.isLoaded(Appodeal.BANNER_BOTTOM) = " + !Appodeal.isLoaded(Appodeal.BANNER_BOTTOM));
            return;
        }
        Log.e("GAWK_ERR","changeAdMob() called, mIsShowKeyboard = " + mIsShowKeyboard);
        if (mIsShowKeyboard) {
            Appodeal.hide(mActivity,Appodeal.BANNER_BOTTOM);
        } else {
            Appodeal.show(mActivity,Appodeal.BANNER_BOTTOM);
        }*/
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

    public void restartNotify(Note note, Notification notification) {
        mNotificationAdapter.restartNotify(note,notification);
    }

    public void deleteNotify(long id) {
        mNotificationAdapter.removeNotify(id);
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
        return ActivityCompat.checkSelfPermission(activity, PERMISSIONS[i]) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkAllPermissions(Activity activity) {
        for (String PERMISSION : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(activity, PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public int getColorByAttr(int attr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = this.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }

    int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}