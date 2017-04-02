package com.gawk.voicenotes;

import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by GAWK on 02.04.2017.
 */

public class HelpActivity extends ParentActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://voicenotes.cofp.ru/");
        initAdMob(false);
    }
}
