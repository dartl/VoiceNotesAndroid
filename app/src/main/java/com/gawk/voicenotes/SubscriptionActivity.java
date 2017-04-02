package com.gawk.voicenotes;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;

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
                try {
                    startBuySubscription(SKU_SMALL_DONATE);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonBigDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startBuySubscription(SKU_BIG_DONATE);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        initAdMob(false);
    }

    public void startBuySubscription(String id_sub) throws RemoteException {

        Bundle bundle = mService.getBuyIntent(3, getPackageName(),
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
    }
}
