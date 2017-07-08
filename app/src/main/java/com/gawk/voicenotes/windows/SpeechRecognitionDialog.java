package com.gawk.voicenotes.windows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.fragments_notes.NewNoteText;

/**
 * Created by GAWK on 08.07.2017.
 */

public class SpeechRecognitionDialog extends DialogFragment {

    private Dialog mDialog;
    private NewNoteText mFragmentParent;
    private ImageView mImageViewVoiceValue;

    public SpeechRecognitionDialog(NewNoteText mFragmentParent) {
        this.mFragmentParent = mFragmentParent;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.speech_recognition_window, null))
                // Add action buttons
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        mDialog = builder.create();
        //mImageViewVoiceValue = (ImageView) mDialog.findViewById(R.id.imageViewVoiceValue);
        return mDialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        mFragmentParent.startRecognize();
    }

    public void changeVoiceValue(int valueDestine) {
        mImageViewVoiceValue.setMinimumWidth(valueDestine);
        mImageViewVoiceValue.setMinimumWidth(valueDestine);
    }

}