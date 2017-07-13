package com.gawk.voicenotes;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gawk.voicenotes.subs.GooglePlaySubs;
import com.gawk.voicenotes.subs.SubsInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GAWK on 30.03.2017.
 */

public class SubscriptionActivity extends ParentActivity {
    private Button buttonSmallDonate, buttonBigDonate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscriptions);

        mGooglePlaySubs = new GooglePlaySubs(this,mService);

        buttonSmallDonate = (Button) findViewById(R.id.buttonSmallDonate);
        buttonBigDonate = (Button) findViewById(R.id.buttonBigDonate);

        buttonSmallDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGooglePlaySubs.startBuySubscription(SubsInterface.SKU_SMALL_DONATE);
            }
        });

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
}
