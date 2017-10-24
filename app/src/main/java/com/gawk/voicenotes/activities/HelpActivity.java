package com.gawk.voicenotes.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.webkit.WebView;

import com.gawk.voicenotes.R;

/**
 * Created by GAWK on 02.04.2017.
 */

public class HelpActivity extends ParentActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("http://voicenotes.cofp.ru/");
        initAdMob(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(R.id.menu_help).setCheckable(true).setChecked(true);
    }
}
