package com.gawk.voicenotes;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gawk.voicenotes.subs.GooglePlaySubs;
import com.gawk.voicenotes.subs.SubsInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by GAWK on 30.03.2017.
 */

public class SubscriptionActivity extends ParentActivity {
    private Button buttonSmallDonate, buttonBigDonate;
    private TextView textViewDonate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscriptions);

        mGooglePlaySubs = new GooglePlaySubs(this,mService);

        buttonSmallDonate = (Button) findViewById(R.id.buttonSmallDonate);
        buttonBigDonate = (Button) findViewById(R.id.buttonBigDonate);
        textViewDonate = (TextView) findViewById(R.id.textViewDonate);

        buttonSmallDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alternativeDonate(); // alternative Donate
                mGooglePlaySubs.startBuySubscription(SubsInterface.SKU_SMALL_DONATE);
            }
        });

        //buttonBigDonate.setVisibility(View.GONE); // alternative Donate
        //textViewDonate.setVisibility(View.GONE); // alternative Donate

        buttonBigDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGooglePlaySubs.startBuySubscription(SubsInterface.SKU_BIG_DONATE);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                addPrice();
            }
        }, 200);
        initAdMob(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(R.id.menu_subs).setCheckable(true).setChecked(true);
    }

    private void addPrice() {
        try {
            ArrayList<JSONObject> allSubscriptionsInfo = mGooglePlaySubs.getAllSubscriptions();
            if (allSubscriptionsInfo != null && allSubscriptionsInfo.size() > 0) {
                for (int i = 0; i < allSubscriptionsInfo.size(); i++) {
                    if (allSubscriptionsInfo.get(i).get("productId").toString().equalsIgnoreCase(SubsInterface.SKU_BIG_DONATE)) {
                        buttonBigDonate.setText(getResources().getText(R.string.donate_big) + " (" + mGooglePlaySubs.getPrice(SubsInterface.SKU_BIG_DONATE) + ")");
                    } else if (allSubscriptionsInfo.get(i).get("productId").toString().equalsIgnoreCase(SubsInterface.SKU_SMALL_DONATE)) {
                        buttonSmallDonate.setText(getResources().getText(R.string.donate_small) + " (" + mGooglePlaySubs.getPrice(SubsInterface.SKU_SMALL_DONATE) + ")");
                    }
                }
            }
        } catch (JSONException | RemoteException e) {
            e.printStackTrace();
        }
    }

    private void alternativeDonate()
    {
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CJJSWW4DWN5YA"));
        startActivity(intent);
    }
}
