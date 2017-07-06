package com.gawk.voicenotes.fragments_notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.preferences.UtilPreferences;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends FragmentParent implements RecognitionListener{
    private TextView editText_NewNoteText;
    private ImageButton imageButton_NewNoteAdd, imageButton_NewNoteClear;

    public NewNoteText() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_note_text, null);
        editText_NewNoteText = (TextView) view.findViewById(R.id.editText_NewNoteText);
        imageButton_NewNoteAdd = (ImageButton) view.findViewById(R.id.imageButton_NewNoteAdd);
        imageButton_NewNoteClear = (ImageButton) view.findViewById(R.id.imageButton_NewNoteClear);

        imageButton_NewNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecognize();
            }
        });

        imageButton_NewNoteClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_NewNoteText.setText("");
            }
        });
        Log.e("GAWK_ERR", "NewNoteText created");
        startRecognize();
        return view;
    }

    private void startRecognize() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {

            //getActivity().startActivityForResult(i, getResources().getInteger(R.integer.constant_request_code_recognize));
        } catch (Exception e) {
            Log.e("GAWK_ERR", "startRecognize() UtilPreferences.getBoolean = " + UtilPreferences.getBoolean(UtilPreferences.CHECK_RECOGNITION,getActivity()));
            if (UtilPreferences.getBoolean(UtilPreferences.CHECK_RECOGNITION,getActivity())) {
                Log.e("GAWK_ERR", "NewNoteText exeption called");
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setTitle(getResources().getText(R.string.new_note_error_speech_recognition_title));  // заголовок
                ad.setMessage(getResources().getText(R.string.new_note_error_speech_recognition)); // сообщение
                ad.setPositiveButton(getResources().getText(R.string.new_note_error_speech_recognition_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startInstallAppRecognize();
                    }
                });
                ad.setNegativeButton(getResources().getText(R.string.new_note_error_speech_recognition_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelInstallAppRecognize();
                    }
                });
                ad.setCancelable(true);
                ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        cancelInstallAppRecognize();
                    }
                });
                ad.show();
            }
        }
    }

    public void startInstallAppRecognize() {
        // Голосовой Поиск имя пакета: com.google.android.voicesearch
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.googlequicksearchbox"));
        // настраиваем флаги, чтобы маркет не попал к в историю нашего приложения (стек Activity)
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // отправляем Intent
        getActivity().startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().finishAffinity();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().finishAndRemoveTask();
        }
    }

    public void cancelInstallAppRecognize() {
        Toast.makeText(getContext(), getResources().getText(R.string.new_note_error_speech_recognition_cancel_message),
                Toast.LENGTH_LONG).show();
    }

    public void setActivityResult(Intent data, int resultCode) {
        boolean normalCode = true;
        if(normalCode) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (editText_NewNoteText.getText().length() == 0) {
                editText_NewNoteText.setText(thingsYouSaid.get(0));
            } else {
                editText_NewNoteText.setText(editText_NewNoteText.getText() + " " + thingsYouSaid.get(0));
            }
        }
    }

    public String getTextNote() {
        if (editText_NewNoteText != null) {
            return String.valueOf(editText_NewNoteText.getText());
        }
        return "";
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        switch (error) {
            case RecognizerIntent.RESULT_AUDIO_ERROR:
                Snackbar.make(getView(), "RESULT_AUDIO_ERROR", Snackbar.LENGTH_LONG).show();
                break;
            case RecognizerIntent.RESULT_CLIENT_ERROR:
                Snackbar.make(getView(), "RESULT_CLIENT_ERROR", Snackbar.LENGTH_LONG).show();
                break;
            case RecognizerIntent.RESULT_NETWORK_ERROR:
                Snackbar.make(getView(), "RESULT_NETWORK_ERROR", Snackbar.LENGTH_LONG).show();
                break;
            case RecognizerIntent.RESULT_NO_MATCH:
                Snackbar.make(getView(), "RESULT_NO_MATCH", Snackbar.LENGTH_LONG).show();
                break;
            case RecognizerIntent.RESULT_SERVER_ERROR:
                Snackbar.make(getView(), "RESULT_SERVER_ERROR", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onResults(Bundle results) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}
