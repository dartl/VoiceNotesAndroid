package com.gawk.voicenotes.windows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.SettingsActivity;
import com.gawk.voicenotes.adapters.preferences.PrefUtil;
import com.gawk.voicenotes.adapters.speech_recognition.LanguageDetailsChecker;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by GAWK on 24.08.2017.
 */

public class SelectLanguageRecognize extends DialogFragment implements View.OnClickListener{
    private Dialog mDlg;
    private ScrollView mView;
    private SettingsActivity mSettingsActivity;
    private Set<String> allSupportedLanguage;
    private RadioGroup mRadioGroup;
    private String oldLang;

    public SelectLanguageRecognize() {
        PrefUtil prefUtil =  new PrefUtil(getActivity());
        init(prefUtil);
    }

    public SelectLanguageRecognize(SettingsActivity mSettingsActivity) {
        this.mSettingsActivity = mSettingsActivity;
        PrefUtil prefUtil =  new PrefUtil(mSettingsActivity);
        init(prefUtil);
    }

    private void init(PrefUtil prefUtil) {
        Set<String> unSortedSet = prefUtil.getStringSet(PrefUtil.SUPPORTED_LANGUAGE_FOR_RECOGNIZE, null);
        // TreeSet
        if (unSortedSet == null) return;
        allSupportedLanguage = new TreeSet<>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        allSupportedLanguage.addAll(unSortedSet);
        oldLang = prefUtil.getString(PrefUtil.SELECTED_LANGUAGE_FOR_RECOGNIZE,"");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        //LayoutInflater inflater = getActivity().getLayoutInflater();

        mRadioGroup = new RadioGroup(mSettingsActivity);
        mRadioGroup.setOrientation(LinearLayout.VERTICAL);

        mView = new ScrollView(mSettingsActivity);
        builder.setView(mView);

        int valueInPixels = (int) getResources().getDimension(R.dimen.list_element_padding);
        mRadioGroup.setPadding(0,valueInPixels,0,valueInPixels);

        RadioButton mRadioButton;

        mRadioButton = new RadioButton(mSettingsActivity);
        mRadioButton.setText(getString(R.string.settings_note_select_language_recognize_default));
        mRadioButton.setPadding(valueInPixels,valueInPixels,valueInPixels,valueInPixels);
        mRadioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT));
        mRadioGroup.addView(mRadioButton);

        if (oldLang==null || oldLang.equals("")) mRadioGroup.check(mRadioButton.getId());


        if (allSupportedLanguage != null) {
            for (String anAllSupportedLanguage : allSupportedLanguage) {
                mRadioButton = new RadioButton(mSettingsActivity);
                Locale locale = new Locale(anAllSupportedLanguage);
                mRadioButton.setText(locale.getDisplayLanguage(locale));
                mRadioButton.setPadding(valueInPixels,valueInPixels,valueInPixels,valueInPixels);
                mRadioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT));
                mRadioGroup.addView(mRadioButton);
                if (oldLang.equals(anAllSupportedLanguage)) mRadioGroup.check(mRadioButton.getId());
            }
        }

        mView.addView(mRadioGroup);

        builder.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int radioButtonID = mRadioGroup.getCheckedRadioButtonId();
                View radioButton = mRadioGroup.findViewById(radioButtonID);
                int idx = mRadioGroup.indexOfChild(radioButton) - 1;
                if(idx == -1) {
                    mSettingsActivity.setLanguageRecognition("");
                    return;
                }

                Iterator<String> itr = allSupportedLanguage.iterator();
                String value = "";
                for(int j = 0; itr.hasNext(); j++) {
                    value = itr.next();
                    if (j == idx) {
                        break;
                    }
                }
                mSettingsActivity.setLanguageRecognition(value);
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        builder.setTitle(R.string.settings_note_select_language_recognize_title);

        mDlg = builder.create();
        return mDlg;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        if (allSupportedLanguage == null) dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
