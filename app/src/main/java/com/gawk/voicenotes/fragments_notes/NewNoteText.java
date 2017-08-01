package com.gawk.voicenotes.fragments_notes;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.windows.SpeechRecognitionDialog;

import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends FragmentParent implements RecognitionListener{
    private TextView editText_NewNoteText;
    private ImageButton imageButton_NewNoteAdd, imageButton_NewNoteClear;
    private SpeechRecognizer mRecognizerIntent;
    private SpeechRecognitionDialog mSpeechRecognitionDialog;
    private boolean mCheckPartialResults = false;
    private String mPartialResultsStart;

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
        mSpeechRecognitionDialog.setmFragmentParent(this);
        mSpeechRecognitionDialog.show(getActivity().getFragmentManager(),"SpeechRecognitionDialog");
    }

    public void endRecognition()  {
        if (mRecognizerIntent != null)
        {
            mRecognizerIntent.cancel();
            mRecognizerIntent.destroy();
        }
    }

    public void startRecognize() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        if (isIntentAvailable(getContext(),i)) { // Проверяем наличие программы для распознования
            mRecognizerIntent = SpeechRecognizer.createSpeechRecognizer(getContext());
            mRecognizerIntent.setRecognitionListener(this);
            mRecognizerIntent.startListening(i);
        } else {
            mSpeechRecognitionDialog.dismiss();
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
        //Log.e("GAWK_ERR","onRmsChanged()");
        mSpeechRecognitionDialog.changeVoiceValue((int) (convertDpToPixel((rmsdB*6), getContext())+
                getResources().getDimension(R.dimen.dialog_recognize_circle_min_size)));
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
        endRecognition();
        mSpeechRecognitionDialog.setInactive();
        mSpeechRecognitionDialog.errorMessage(error);
    }

    @Override
    public void onResults(Bundle results) {
        Log.e("GAWK_ERR","onResults(Bundle results)");
        ArrayList<String> thingsYouSaid = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (thingsYouSaid != null) {
            if (mPartialResultsStart.length() == 0) {
                editText_NewNoteText.setText(thingsYouSaid.get(0));
            } else {
                editText_NewNoteText.setText(mPartialResultsStart + thingsYouSaid.get(0));
            }
            mSpeechRecognitionDialog.dismiss();
        }
        endRecognition();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.e("GAWK_ERR","onPartialResults(Bundle partialResults)");
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

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void onPause() {
        if (mRecognizerIntent != null) {
            mRecognizerIntent.destroy();
        }
        super.onPause();
    }
}
