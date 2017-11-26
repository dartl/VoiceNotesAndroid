package com.gawk.voicenotes.adapters.speech_recognition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.gawk.voicenotes.adapters.preferences.PrefUtil;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by GAWK on 26.11.2017.
 */

public class LanguageDetailsChecker extends BroadcastReceiver {
    private List<String> supportedLanguages;

    private String languagePreference;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle results = getResultExtras(true);
        if (results.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE))
        {
            languagePreference =
                    results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
        }
        if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES))
        {
            supportedLanguages =
                    results.getStringArrayList(
                            RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
            assert supportedLanguages != null;
            for(int i = 0; i < supportedLanguages.size(); i++) {
                supportedLanguages.set(i, supportedLanguages.get(i).substring(0,2));
            }
            Set<String> foo = new HashSet<>(supportedLanguages);



            Log.e("GAWK_ERR", "foo = " + foo);

            PrefUtil mPrefUtil = new PrefUtil(context);

            mPrefUtil.saveStringSet(PrefUtil.SUPPORTED_LANGUAGE_FOR_RECOGNIZE, foo);
        }
    }
}
