package com.gawk.voicenotes.activities;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.preferences.PrefUtil;
import com.gawk.voicenotes.windows.SelectIntervalDialog;
import com.gawk.voicenotes.windows.SelectTheme;

/**
 * Created by GAWK on 30.03.2017.
 */

public class SettingsActivity extends ParentActivity {
    private int CODE_RESULT_SOUND = 1;
    private Button addShortcut, buttonSelectSound, mButtonRepetitionView, mSelectTheme;
    private TextView textViewSelectSound;
    private PrefUtil mPrefUtil;
    private TextView mTextViewRepetition;
    private SettingsActivity mSettingsActivity;
    private SelectTheme mSelectThemeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mSettingsActivity = this;
        mPrefUtil = new PrefUtil(this);

        addShortcut = (Button) findViewById(R.id.buttonAddShortcut);
        addShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installIconAddNote();
            }
        });

        buttonSelectSound = (Button) findViewById(R.id.buttonSelectSound);
        buttonSelectSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAudio();
            }
        });

        textViewSelectSound = (TextView) findViewById(R.id.textViewSelectSound);
        mButtonRepetitionView = (Button) findViewById(R.id.buttonRepetitionView);

        mTextViewRepetition = (TextView) findViewById(R.id.textViewRepetition);
        setIntervalNotification(mPrefUtil.getLong(mPrefUtil.NOTIFICATION_INTERVAL,0));

        mButtonRepetitionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectIntervalDialog selectIntervalDialog = new SelectIntervalDialog(mSettingsActivity);
                selectIntervalDialog.show(getFragmentManager(),"selectIntervalDialog");
            }
        });

        mSelectTheme = (Button) findViewById(R.id.buttonSelectTheme);
        mSelectTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectThemeDialog = new SelectTheme();
                mSelectThemeDialog.show(getFragmentManager(),"SelectTheme");
            }
        });

        String url = mPrefUtil.getString(PrefUtil.NOTIFICATION_SOUND,"");
        setSoundTitle(url);
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(R.id.menu_settings).setCheckable(true).setChecked(true);
    }

    protected void installIconAddNote() {
        final Intent shortcutIntent = new Intent(this, CreateNoteActivity.class);
        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        shortcutIntent.setAction(Intent.ACTION_CREATE_DOCUMENT);
        // Sets the custom shortcut's title
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.menu_add));
        // Set the custom shortcut icon
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.icon175x175));
        // add the shortcut
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(intent);
        Snackbar.make(getCurrentFocus(), getString(R.string.success), Snackbar.LENGTH_LONG).show();
    }

    private void saveString(String s) {
        mPrefUtil.saveString(PrefUtil.NOTIFICATION_SOUND,s);
    }

    public void selectAudio() {
        String url = mPrefUtil.getString(PrefUtil.NOTIFICATION_SOUND,"");
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, url);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, url);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
                getResources().getString(R.string.settings_notification_sound));
        startActivityForResult(intent, CODE_RESULT_SOUND);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_RESULT_SOUND && data != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                String url;
                if (uri != null) {
                    url = uri.toString();
                    setSoundTitle(url);
                } else {
                    url = RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI;
                }
                saveString(url);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setSoundTitle(String s) {
        Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(s));
        if (r != null) {
            textViewSelectSound.setText(r.getTitle(this));
        }
    }

    public void setIntervalNotification(long time) {
        mPrefUtil.saveLong(PrefUtil.NOTIFICATION_INTERVAL,time);
        final int timeInterval = (int) time/60000;
        switch (timeInterval) {
            case 5:
                mTextViewRepetition.setText(getText(R.string.settings_notification_repetition_text) + " - " + getText(R.string.settings_notification_repetition_select5));
                break;
            case 10:
                mTextViewRepetition.setText(getText(R.string.settings_notification_repetition_text) + " - " + getText(R.string.settings_notification_repetition_select10));
                break;
            case 15:
                mTextViewRepetition.setText(getText(R.string.settings_notification_repetition_text) + " - " + getText(R.string.settings_notification_repetition_select15));
                break;
            case 30:
                mTextViewRepetition.setText(getText(R.string.settings_notification_repetition_text) + " - " + getText(R.string.settings_notification_repetition_select30));
                break;
            case 60:
                mTextViewRepetition.setText(getText(R.string.settings_notification_repetition_text) + " - " + getText(R.string.settings_notification_repetition_select60));
                break;
        }
    }

    public void changeTheme(int color) {
        mSelectThemeDialog.dismiss();
        PrefUtil prefUtil = new PrefUtil(this);
        int theme = prefUtil.getInt(PrefUtil.THEME,-1);
        switch (color) {
            case R.color.tealColor500:
                theme = R.style.GawkMaterialTheme_Base;
                break;
            case R.color.deepPurpleColor500:
                theme = R.style.GawkMaterialTheme_BaseDeepPurple;
                break;
            case R.color.colorGrey500:
                theme = R.style.GawkMaterialTheme_BaseGrey;
                break;
            case R.color.red500:
                theme = R.style.GawkMaterialTheme_BaseRed;
                break;
            case R.color.Pink500:
                theme = R.style.GawkMaterialTheme_BasePink;
                break;
            case R.color.Purple500:
                theme = R.style.GawkMaterialTheme_BasePurple;
                break;
            case R.color.Indigo500:
                theme = R.style.GawkMaterialTheme_BaseIndigo;
                break;
            case R.color.Blue500:
                theme = R.style.GawkMaterialTheme_BaseBlue;
                break;
            case R.color.LightBlue500:
                theme = R.style.GawkMaterialTheme_BaseLightBlue;
                break;
            case R.color.Cyan500:
                theme = R.style.GawkMaterialTheme_BaseCyan;
                break;
            case R.color.Green500:
                theme = R.style.GawkMaterialTheme_BaseGreen;
                break;
            case R.color.LightGreen500:
                theme = R.style.GawkMaterialTheme_BaseLightGreen;
                break;
            case R.color.Lime500:
                theme = R.style.GawkMaterialTheme_BaseLime;
                break;
            case R.color.Yellow500:
                theme = R.style.GawkMaterialTheme_BaseYellow;
                break;
            case R.color.Amber500:
                theme = R.style.GawkMaterialTheme_BaseAmber;
                break;
            case R.color.Orange500:
                theme = R.style.GawkMaterialTheme_BaseOrange;
                break;
            case R.color.DeepOrange500:
                theme = R.style.GawkMaterialTheme_BaseDeepOrange;
                break;
            case R.color.Brown500:
                theme = R.style.GawkMaterialTheme_BaseBrown;
                break;
        }
        recreate();
        prefUtil.saveInt(PrefUtil.THEME,theme);
    }
}