package com.gawk.voicenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by GAWK on 30.03.2017.
 */

public class SettingsActivity extends ParentActivity {
    private Button addShortcut;
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
}
