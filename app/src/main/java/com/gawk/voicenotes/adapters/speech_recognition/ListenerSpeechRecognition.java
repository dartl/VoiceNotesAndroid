package com.gawk.voicenotes.adapters.speech_recognition;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.speech_recognition.dialogs.SpeechRecognitionDialog;

import java.util.ArrayList;

/**
 * Created by GAWK on 24.10.2017.
 */

public class ListenerSpeechRecognition implements RecognitionListener {
    private SpeechRecognitionDialog mSpeechRecognitionDialog;
    private ActionSpeechRecognition mActionSpeechRecognition;
    private FragmentActivity mFragmentActivity;
    private ArrayList<String> mSuggestions = new ArrayList<>();
    private EditText mEditText;
    private String mOldText = "";

    public ListenerSpeechRecognition(FragmentActivity mFragmentActivity, EditText mEditText) {
        if (!mEditText.equals("")) mOldText = mEditText.getText().toString();
        this.mEditText = mEditText;
        this.mFragmentActivity = mFragmentActivity;
        this.mSpeechRecognitionDialog = new SpeechRecognitionDialog();
        this.mActionSpeechRecognition = new ActionSpeechRecognition(this.mFragmentActivity, this.mFragmentActivity.getParent(), this.mSpeechRecognitionDialog, this);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e("GAWK_ERR","onReadyForSpeech()");
        mSpeechRecognitionDialog.setActive();
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e("GAWK_ERR","onBeginningOfSpeech()");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        if (mSpeechRecognitionDialog != null) {
            int size = (int) (mActionSpeechRecognition.convertDpToPixel((rmsdB*6), mFragmentActivity)+
                    mFragmentActivity.getResources().getDimension(R.dimen.dialog_recognize_circle_min_size));
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
            addText(thingsYouSaid.get(0));
            mEditText.setText(getFullText());
            mSpeechRecognitionDialog.dismiss();
        }
        mActionSpeechRecognition.endRecognition();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> thingsYouSaid = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (thingsYouSaid != null) {
            String text = getFullText() + thingsYouSaid.get(0);
            mEditText.setText(text);
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.e("GAWK_ERR","onEvent(int eventType, Bundle params)");
    }

    public void show() {
        mSpeechRecognitionDialog.setFragmentParent(mActionSpeechRecognition);
        mSpeechRecognitionDialog.show(mFragmentActivity.getFragmentManager(),"SpeechRecognitionDialog");
    }

    public void pause() {
        if (mActionSpeechRecognition != null) {
            mActionSpeechRecognition.destroy();
        }
    }

    public void clearSuggestions() {
        mSuggestions = new ArrayList<>();
    }

    private String getFullText() {
        String fullText = mOldText;
        for (int i = 0; i < mSuggestions.size(); i++) {
            fullText += mSuggestions.get(i) + " ";
        }
        return fullText;
    }

    private void addText(String newText) {
        newText = newText.trim();
        if (newText.length() != 0) {
            String[] partsNewText = newText.split(".");
            if (partsNewText.length > 0 ) {
                for (String partNewText : partsNewText) {
                    addTextSuggestions(partNewText, true);
                }
            } else {
                addTextSuggestions(newText, false);
            }
        }
    }

    private void addTextSuggestions(String newText, boolean dot) {
        String doted = "";
        if (dot) {
            doted = ".";
        }
        if (mSuggestions.size() == 0 || mSuggestions.get(mSuggestions.size() - 1).contains(".")) {
            newText = newText.substring(0, 1).toUpperCase() + newText.substring(1);
            mSuggestions.add(newText + doted);
        } else {
            mSuggestions.set(mSuggestions.size() - 1, mSuggestions.get(mSuggestions.size() - 1) + " " + newText + doted);
        }
    }
}
