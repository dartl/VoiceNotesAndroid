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

        buttonSmallDonate = (Button) findViewById(R.id.buttonSmallDonate);
        buttonBigDonate = (Button) findViewById(R.id.buttonBigDonate);

        buttonSmallDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBuySubscription(SKU_SMALL_DONATE);
            }
        });

        buttonBigDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBuySubscription(SKU_BIG_DONATE);
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

    public void startBuySubscription(String id_sub) {
        Bundle bundle = null;
        try {
            bundle = mService.getBuyIntent(3, getPackageName(),
                    id_sub, "subs", "");
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
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void addPrice() {
        try {
            ArrayList<JSONObject> allSubscriptionsInfo = getAllSubscriptions();
            Log.e("GAWK_ERR","addPrice() called. allSubscriptionsInfo = " + allSubscriptionsInfo);
            if (allSubscriptionsInfo != null && allSubscriptionsInfo.size() > 0) {

                for (int i = 0; i < allSubscriptionsInfo.size(); i++) {
                    Log.e("GAWK_ERR", "FOR (i=" + i + "):" +allSubscriptionsInfo.get(i).get("productId").toString());
                    if (allSubscriptionsInfo.get(i).get("productId").toString().equalsIgnoreCase(SKU_BIG_DONATE)) {
                        Log.e("GAWK_ERR", "CALL " +SKU_BIG_DONATE);
                        buttonBigDonate.setText(getResources().getText(R.string.donate_big) + " (" + allSubscriptionsInfo.get(i).get("price").toString() + ")");
                    } else if (allSubscriptionsInfo.get(i).get("productId").toString().equalsIgnoreCase(SKU_SMALL_DONATE)) {
                        Log.e("GAWK_ERR", SKU_SMALL_DONATE);
                        buttonSmallDonate.setText(getResources().getText(R.string.donate_small) + " (" + allSubscriptionsInfo.get(i).get("price").toString() + ")");
                    }
                }
                ;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
