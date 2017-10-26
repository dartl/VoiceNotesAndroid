package com.gawk.voicenotes.adapters.speech_recognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.speech_recognition.dialogs.SpeechRecognitionDialog;

import java.util.List;
import java.util.Locale;

/**
 * Created by GAWK on 31.08.2017.
 */

public class ActionSpeechRecognition {
    private Context mContext;
    private Activity mActivity;
    private SpeechRecognizer mRecognizerIntent;
    private SpeechRecognitionDialog mSpeechRecognitionDialog;
    private RecognitionListener mRecognitionListener;

    public ActionSpeechRecognition(Context mContext, Activity mActivity, SpeechRecognitionDialog mSpeechRecognitionDialog, RecognitionListener mRecognitionListener) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mSpeechRecognitionDialog = mSpeechRecognitionDialog;
        this.mRecognitionListener = mRecognitionListener;
    }

    public void startRecognize() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        if (isIntentAvailable(mContext,i)) { // Проверяем наличие программы для распознования
            mRecognizerIntent = SpeechRecognizer.createSpeechRecognizer(mContext);
            mRecognizerIntent.setRecognitionListener(mRecognitionListener);
            mRecognizerIntent.startListening(i);
        } else {
            mSpeechRecognitionDialog.dismiss();
            AlertDialog.Builder ad = new AlertDialog.Builder(mContext);
            ad.setTitle(mContext.getResources().getText(R.string.new_note_error_speech_recognition_title));  // заголовок
            ad.setMessage(mContext.getResources().getText(R.string.new_note_error_speech_recognition)); // сообщение
            ad.setPositiveButton(mContext.getResources().getText(R.string.new_note_error_speech_recognition_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startInstallAppRecognize();
                }
            });
            ad.setNegativeButton(mContext.getResources().getText(R.string.new_note_error_speech_recognition_cancel), new DialogInterface.OnClickListener() {
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

    public void endRecognition() {
        if (mRecognizerIntent != null) {
            mRecognizerIntent.cancel();
        }
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public void startInstallAppRecognize() {
        // Голосовой Поиск имя пакета: com.google.android.voicesearch
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.googlequicksearchbox"));
        // настраиваем флаги, чтобы маркет не попал к в историю нашего приложения (стек Activity)
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // отправляем Intent
        mContext.startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mActivity.finishAffinity();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.finishAndRemoveTask();
        }
    }

    public void cancelInstallAppRecognize() {
        Toast.makeText(mContext, mContext.getResources().getText(R.string.new_note_error_speech_recognition_cancel_message),
                Toast.LENGTH_LONG).show();
    }

    public void destroy() {
        if (mRecognizerIntent != null) {
            mRecognizerIntent.cancel();
        }
    }

    public float convertDpToPixel(float dp, Context context){
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
