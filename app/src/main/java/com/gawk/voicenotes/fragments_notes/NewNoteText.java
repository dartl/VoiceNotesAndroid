package com.gawk.voicenotes.fragments_notes;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionSpeechRecognition;
import com.gawk.voicenotes.windows.SpeechRecognitionDialog;

import java.util.ArrayList;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends FragmentParent implements RecognitionListener{
    private TextView editText_NewNoteText;
    private FloatingActionButton imageButton_NewNoteAdd, imageButton_NewNoteClear;
    private SpeechRecognitionDialog mSpeechRecognitionDialog;
    private boolean mCheckPartialResults = false;
    private String mPartialResultsStart;
    private ActionSpeechRecognition mActionSpeechRecognition;

    public NewNoteText() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_note_text, null);
        editText_NewNoteText = view.findViewById(R.id.editText_NewNoteText);
        imageButton_NewNoteAdd = view.findViewById(R.id.imageButton_NewNoteAdd);
        imageButton_NewNoteClear =  view.findViewById(R.id.imageButton_NewNoteClear);

        imageButton_NewNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecognizeDialog();
            }
        });

        imageButton_NewNoteClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_NewNoteText.setText("");
            }
        });

        showRecognizeDialog();
        return view;
    }

    private void showRecognizeDialog() {
        mSpeechRecognitionDialog = new SpeechRecognitionDialog();
        mActionSpeechRecognition = new ActionSpeechRecognition(getContext(),getActivity(),mSpeechRecognitionDialog,this);
        mSpeechRecognitionDialog.setFragmentParent(mActionSpeechRecognition);
        mSpeechRecognitionDialog.show(getActivity().getFragmentManager(),"SpeechRecognitionDialog");
    }

    public String getTextNote() {
        if (editText_NewNoteText != null) {
            return String.valueOf(editText_NewNoteText.getText());
        }
        return "";
    }

    // RecognitionListener

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e("GAWK_ERR","onReadyForSpeech()");
        mSpeechRecognitionDialog.setActive();
        mCheckPartialResults = false;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e("GAWK_ERR","onBeginningOfSpeech()");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        if (mSpeechRecognitionDialog != null) {
            int size = (int) (mActionSpeechRecognition.convertDpToPixel((rmsdB*6), getContext())+
                    getResources().getDimension(R.dimen.dialog_recognize_circle_min_size));
            if (size >= 0) {
                mSpeechRecognitionDialog.changeVoiceValue(size);
            }
        }
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e("GAWK_ERR","onBufferReceived(byte[] buffer)");
    }

    @Override
    public void onEndOfSpeech() {
        Log.e("GAWK_ERR","onEndOfSpeech()");
    }

    @Override
    public void onError(int error) {
        Log.e("GAWK_ERR","onError(int error) = " + error);
        mActionSpeechRecognition.endRecognition();
        mSpeechRecognitionDialog.setInactive();
        mSpeechRecognitionDialog.errorMessage(error);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> thingsYouSaid = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (thingsYouSaid != null) {
            if (mPartialResultsStart.length() == 0) {
                editText_NewNoteText.setText(thingsYouSaid.get(0));
            } else {
                editText_NewNoteText.setText(mPartialResultsStart + thingsYouSaid.get(0));
            }
            mSpeechRecognitionDialog.dismiss();
        }
        mActionSpeechRecognition.endRecognition();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        if (!mCheckPartialResults) {
            mPartialResultsStart = editText_NewNoteText.getText().toString();
            mCheckPartialResults = true;
            if (editText_NewNoteText.length() > 0) {
                mPartialResultsStart += " ";
            }
        }
        ArrayList<String> thingsYouSaid = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        editText_NewNoteText.setText(mPartialResultsStart+thingsYouSaid.get(0));
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.e("GAWK_ERR","onEvent(int eventType, Bundle params)");
    }


    @Override
    public void onPause() {
        if (mActionSpeechRecognition != null) {
            mActionSpeechRecognition.destroy();
        }
        super.onPause();
    }
}
