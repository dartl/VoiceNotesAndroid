package com.gawk.voicenotes.windows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.fragments_notes.NewNoteText;

/**
 * Created by GAWK on 08.07.2017.
 */

public class SpeechRecognitionDialog extends DialogFragment {

    private NewNoteText mFragmentParent;
    private ImageView mImageViewVoiceValue;
    private ImageButton mImageButtonVoice;
    private TextView mTextViewMainText;
    private boolean mActive = false;

    public SpeechRecognitionDialog(NewNoteText mFragmentParent) {
        this.mFragmentParent = mFragmentParent;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.speech_recognition_window, null);
        mImageViewVoiceValue = (ImageView) view.findViewById(R.id.imageViewVoiceValue);
        mTextViewMainText = (TextView) view.findViewById(R.id.textViewMainText);
        mImageButtonVoice = (ImageButton) view.findViewById(R.id.imageButtonVoice);

        builder.setView(view);

        Dialog dlg = builder.create();
        Window window = dlg.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;

        mImageButtonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mActive) {
                    mFragmentParent.startRecognize();
                    setActive();
                }
            }
        });

        return dlg;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        mFragmentParent.startRecognize();
        mActive = true;
    }

    public void changeVoiceValue(int valueDestine) {
        mImageViewVoiceValue.getLayoutParams().height = valueDestine;
        mImageViewVoiceValue.getLayoutParams().width = valueDestine;
        mImageViewVoiceValue.requestLayout();
    }

    public void changeVisibleSpeak(int state) {
        mTextViewMainText.setVisibility(state);
    }

    public void setInactive() {
        mImageButtonVoice.setBackgroundResource(R.drawable.speech_recognition_circle_grey);
        mTextViewMainText.setText(getText(R.string.new_note_speech_recognition_start));
        mActive = false;
    }

    public void setActive() {
        mTextViewMainText.setText(getText(R.string.new_note_speech_recognition_message));
        mImageButtonVoice.setBackgroundResource(R.drawable.speech_recognition_circle);
        changeVisibleSpeak(View.VISIBLE);
        mActive = true;
    }
}