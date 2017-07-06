package com.gawk.voicenotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gawk.voicenotes.adapters.OpenFileDialog;
import com.gawk.voicenotes.adapters.PrefUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by GAWK on 30.03.2017.
 */

public class SettingsActivity extends ParentActivity {
    private int CODE_RESULT_SOUND = 1;
    private Button addShortcut, buttonSelectSound;
    private TextView textViewSelectSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

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

        PrefUtil prefUtil = new PrefUtil(this);
        String url = prefUtil.getString(PrefUtil.NOTIFICATION_SOUND,"");
        setSoundTitle(url);
    }

    protected void installIconAddNote() {
        final Intent shortcutIntent = new Intent(this, NewNote.class);
        final Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        shortcutIntent.setAction(Intent.ACTION_CREATE_DOCUMENT);
        // Sets the custom shortcut's title
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.menu_add));
        // Set the custom shortcut icon
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.icon175x175_big));
        // add the shortcut
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(intent);
        Snackbar.make(getCurrentFocus(), getString(R.string.success), Snackbar.LENGTH_LONG).show();
    }

    private void saveString(String s) {
        PrefUtil prefUtil = new PrefUtil(this);
        prefUtil.saveString(PrefUtil.NOTIFICATION_SOUND,s);
    }

    public void selectAudio() {
        PrefUtil prefUtil = new PrefUtil(this);
        String url = prefUtil.getString(PrefUtil.NOTIFICATION_SOUND,"");
        Log.e("GAWK_ERR","selectAudio(). url = " + url);
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
            Log.e("GAWK_ERR","onActivityResult. requestCode = " + requestCode + "; data = " + data.toString());
            Log.e("GAWK_ERR","RingtoneManager.EXTRA_RINGTONE_PICKED_URI = " + data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
            if (resultCode == RESULT_OK) {
                Log.e("GAWK_ERR","onActivityResult (resultCode == CODE_RESULT_SOUND)");
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                String url;
                if (uri != null) {
                    Log.e("GAWK_ERR","uri.toString() = " + uri.toString());
                    url = uri.toString();
                    setSoundTitle(url);
                } else {
                    url = RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI;
                }
                saveString(url);
                Log.e("GAWK_ERR",url);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setSoundTitle(String s) {
        Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(s));
        textViewSelectSound.setText(r.getTitle(this));
    }
}
