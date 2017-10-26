package com.gawk.voicenotes.adapters.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.gawk.voicenotes.R;

/**
 * Created by GAWK on 07.08.2017.
 */

public class SocialShare implements View.OnClickListener {
    private Context context;

    public SocialShare(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        String url = "";
        switch (v.getId()) {
            case R.id.buttonFacebook:
                url = "https://www.facebook.com/sharer/sharer.php?u=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.gawk.voicenotes&amp;src=sdkpreparse";
                break;
            case R.id.buttonVk:
                url = "https://vk.com/share.php?url=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.gawk.voicenotes";
                break;
            case R.id.buttonGoogle:
                url ="https://plus.google.com/share?app=110&url=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dcom.gawk.voicenotes";
                break;
            case R.id.buttonTwitter:
                url = "https://twitter.com/intent/tweet?text="+context.getText(R.string.app_name)+"&url=https://play.google.com/store/apps/details?id=com.gawk.voicenotes";
                break;
            case R.id.buttonLinkedln:
                url = "https://www.linkedin.com/shareArticle?mini=true&url=https%3A//play.google." +
                        "com/store/apps/details?id=com.gawk.voicenotes&title="+context.getText(R.string.app_name)+"&summary=&source=";
                break;
            case R.id.buttonOdnoklassnik:
                url = "http://www.odnoklassniki.ru/dk?st.cmd=addShare&st.s=1&st.comments="+
                        context.getText(R.string.app_name)+"&st._surl=https://play.google.com/store/apps/details?id=com.gawk.voicenotes";
                break;
            default:
                Log.e("GAWK_ERR", "SocialShare() click undefined button");
                break;
        }
        if (url.length() > 0) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }
}
