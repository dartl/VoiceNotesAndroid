package com.gawk.voicenotes.adapters.subs;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;
import com.gawk.voicenotes.activities.ParentActivity;
import com.gawk.voicenotes.adapters.preferences.PrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by GAWK on 13.07.2017.
 */

public class GooglePlaySubs implements SubsInterface {
    private ParentActivity mActivity;
    private IInAppBillingService mService;

    public GooglePlaySubs(ParentActivity mActivity, IInAppBillingService mService) {
        this.mActivity = mActivity;
        this.mService = mService;
    }

    @Override
    public void startBuySubscription(String id_sub) {
        Bundle bundle = null;
        try {
            bundle = mService.getBuyIntent(3, mActivity.getPackageName(),
                    id_sub, "subs", "");
            PendingIntent pendingIntent = bundle.getParcelable(RESPONSE_BUY_INTENT);
            if (bundle.getInt("RESPONSE_CODE") == BILLING_RESPONSE_RESULT_OK) {
                // Start purchase flow (this brings up the Google Play UI).
                // Result will be delivered through onActivityResult().
                try {
                    mActivity.startIntentSenderForResult(pendingIntent.getIntentSender(), RC_BUY, new Intent(),
                            Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPrice(String id_sub) {
        try {
            ArrayList<JSONObject> allSubscriptionsInfo = getAllSubscriptions();
            if (allSubscriptionsInfo != null && allSubscriptionsInfo.size() > 0) {
                for (int i = 0; i < allSubscriptionsInfo.size(); i++) {
                    if (allSubscriptionsInfo.get(i).get("productId").toString().equalsIgnoreCase(id_sub)) {
                        return allSubscriptionsInfo.get(i).get("price").toString();
                    }
                }
            }
        } catch (JSONException | RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public ArrayList<JSONObject> getAllSubscriptions() throws RemoteException {
        ArrayList<String> skuList = new ArrayList<> ();
        skuList.add(SubsInterface.SKU_BIG_DONATE);
        skuList.add(SubsInterface.SKU_SMALL_DONATE);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        if ( mService == null) {
            return null;
        }
        Bundle skuDetails = mService.getSkuDetails(3,
                mActivity.getPackageName(), "subs", querySkus);
        int response = skuDetails.getInt("RESPONSE_CODE");
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        if (response == 0) {
            ArrayList<String> responseList
                    = skuDetails.getStringArrayList("DETAILS_LIST");

            for (String thisResponse : responseList) {
                JSONObject object;
                try {
                    object = new JSONObject(thisResponse);
                    jsonObjects.add(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObjects;
    }

    @Override
    public void checkBuySubs() throws RemoteException {
        PrefUtil prefUtil = new PrefUtil(mActivity);
        Bundle activeSubs = mService.getPurchases(3, mActivity.getPackageName(),
                "subs", "");

        int response = activeSubs.getInt("RESPONSE_CODE");
        if (response == 0) {
            ArrayList<String> ownedSkus =
                    activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            if (ownedSkus.size() > 0) {
                prefUtil.saveInt(DONATE_PREF,2);
                mActivity.initAdMob(false);
            } else {
                prefUtil.saveInt(DONATE_PREF,1);
            }
        }
    }

    @Override
    public void subsSetActive() {
        PrefUtil prefUtil = new PrefUtil(mActivity);
        prefUtil.saveInt(DONATE_PREF,2);
    }

    @Override
    public int subsGetActive() {
        PrefUtil prefUtil = new PrefUtil(mActivity);
        return prefUtil.getInt(DONATE_PREF,0);
    }

}
