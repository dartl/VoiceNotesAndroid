package com.gawk.voicenotes.speech.recognition.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.speech.recognition.ActionSpeechRecognition;

/**
 * Created by GAWK on 08.07.2017.
 */

public class SpeechRecognitionDialog extends DialogFragment {

    private ActionSpeechRecognition mFragmentParent;
    private ImageView mImageViewVoiceValue;
    private ImageButton mImageButtonVoice;
    private TextView mTextViewMainText;
    private RelativeLayout mAreaRecognition;
    private Button mButtonClose, mButtonFix;
    private Dialog mDlg;
    private boolean mActive = false;
    private int mErrorCode = -1;

    public SpeechRecognitionDialog() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_speech_recognition, null);
        mImageViewVoiceValue = view.findViewById(R.id.imageViewVoiceValue);
        mTextViewMainText = view.findViewById(R.id.textViewMainText);
        mImageButtonVoice =  view.findViewById(R.id.imageButtonVoice);
        mButtonClose = view.findViewById(R.id.buttonClose);
        mButtonFix = view.findViewById(R.id.buttonFix);
        mAreaRecognition = view.findViewById(R.id.areaRecognition);

        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismiss(mDlg);
            }
        });

        mButtonFix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mErrorCode) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        startInstalledAppDetailsActivity(getActivity());
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        startOpenSettings();
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        break;
                }
            }
        });

        builder.setView(view);

        mDlg = builder.create();

        Window window = mDlg.getWindow();
        assert window != null;
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;

        mImageButtonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mActive) {
                    setActive();
                    if (mFragmentParent != null) {
                        mFragmentParent.startRecognize();
                    }
                } else {
                    setInactive();
                    if (mFragmentParent != null) {
                        changeVoiceValue(100);
                        mFragmentParent.endRecognition();
                    }
                }
            }
        });

        return mDlg;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        mFragmentParent.startRecognize();
        mActive = true;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mFragmentParent != null) {
            mFragmentParent.endRecognition();
        }
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mFragmentParent != null) {
            mFragmentParent.endRecognition();
        }
        super.onDismiss(dialog);
    }

    public void changeVoiceValue(int valueDestine) {
        try {
            mImageViewVoiceValue.getLayoutParams().height = valueDestine;
            mImageViewVoiceValue.getLayoutParams().width = valueDestine;
            mImageViewVoiceValue.requestLayout();
        } catch (NullPointerException e) {
            e.printStackTrace(System.err);
        }
    }

    public void changeVisibleSpeak(int state) {
        mTextViewMainText.setVisibility(state);
        mImageButtonVoice.setVisibility(state);
    }

    public void setInactive() {
        if (isAdded()) {
            mImageButtonVoice.setBackgroundResource(R.drawable.speech_recognition_circle_grey);
            mTextViewMainText.setText(getText(R.string.new_note_speech_recognition_start));
            mActive = false;
        }
    }

    public void setActive() {
        if (isAdded()) {
            mTextViewMainText.setText(getText(R.string.new_note_speech_recognition_message));
            mImageButtonVoice.setBackgroundResource(R.drawable.speech_recognition_circle);
            changeVisibleSpeak(View.VISIBLE);
            mActive = true;
        }
    }

    public void errorMessage(int error) {
        String mErrorMessage = "Error";
        mErrorCode = error;
        if (isAdded()) {
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_audio);
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_AUDIO");
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_client);
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_CLIENT");
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_insufficient_permissions);
                    mButtonFix.setVisibility(View.VISIBLE);
                    // Ошибка доступа к разрешениям
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS");
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    mFragmentParent.endRecognition();
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_network);
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_NETWORK");
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_network_timeout);
                    // Например вышли из активности не дожидаясь распознавания
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_NETWORK_TIMEOUT");
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_no_match);
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_NO_MATCH");
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    mFragmentParent.endRecognition();
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_recognizer_busy);
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_RECOGNIZER_BUSY");
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_server);
                    if (!hasConnection(getActivity())) {
                        mButtonFix.setVisibility(View.VISIBLE);
                        mErrorMessage = getString(R.string.new_note_speech_recognition_error_server_fix);
                    }
                    // если нет инет и эта ошибка - не хватает пакета локализации
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_SERVER");
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    mErrorMessage = getString(R.string.new_note_speech_recognition_error_speech_timeout);
                    Log.e("GAWK_ERR","onError(int error) - SpeechRecognizer.ERROR_SPEECH_TIMEOUT");
                    break;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTextViewMainText.setTextAppearance(R.style.GawkMaterialTheme_SpeechRecognitionTextError);
            } else {
                mTextViewMainText.setTextAppearance(getActivity(),R.style.GawkMaterialTheme_SpeechRecognitionTextError);
            }
            mTextViewMainText.setText(mErrorMessage);
            changeVisibleSpeak(View.VISIBLE);
            mAreaRecognition.setVisibility(View.GONE);
        }
    }


    public void startInstalledAppDetailsActivity(Activity context) {
        ActivityCompat.requestPermissions(
                context,
                new String[] {Manifest.permission.RECORD_AUDIO},
                136
        );
        onDismiss(mDlg);
    }

    public void startOpenSettings() {
        Intent callGPSSettingIntent = new Intent(Intent.ACTION_MAIN);

        startActivity(callGPSSettingIntent);
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }

    public ActionSpeechRecognition getFragmentParent() {
        return mFragmentParent;
    }

    public void setFragmentParent(ActionSpeechRecognition mFragmentParent) {
        this.mFragmentParent = mFragmentParent;
    }
}