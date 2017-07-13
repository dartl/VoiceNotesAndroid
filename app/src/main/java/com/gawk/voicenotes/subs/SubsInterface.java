package com.gawk.voicenotes.subs;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GAWK on 13.07.2017.
 */

public interface SubsInterface {
    String RESPONSE_BUY_INTENT = "BUY_INTENT";
    int BILLING_RESPONSE_RESULT_OK = 0;
    int RC_BUY = 1001;
    String SKU_SMALL_DONATE = "donation_one_dollor";
    String SKU_BIG_DONATE = "big_donate";
    String DONATE_PREF = "donate_app";

    void startBuySubscription(String id_sub);
    String getPrice(String id_sub);
    ArrayList<JSONObject> getAllSubscriptions() throws RemoteException;
    void checkBuySubs() throws RemoteException;
    void subsSetActive();
    int subsGetActive();
}
