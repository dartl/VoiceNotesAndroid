package com.gawk.voicenotes.windows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.SettingsActivity;

/**
 * Created by GAWK on 24.08.2017.
 */

public class SelectIntervalDialog extends DialogFragment implements View.OnClickListener{
    private Dialog mDlg;
    private View mView;
    private SettingsActivity mSettingsActivity;
    private Button mButton5, mButton10, mButton15, mButton30, mButton60;

    public SelectIntervalDialog(SettingsActivity mSettingsActivity) {
        this.mSettingsActivity = mSettingsActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mView = inflater.inflate(R.layout.dialog_settings_select_interval, null);
        builder.setView(mView);

        mButton5 = mView.findViewById(R.id.buttonIntervalSelect5);
        mButton10 = mView.findViewById(R.id.buttonIntervalSelect10);
        mButton15 = mView.findViewById(R.id.buttonIntervalSelect15);
        mButton30 = mView.findViewById(R.id.buttonIntervalSelect30);
        mButton60 = mView.findViewById(R.id.buttonIntervalSelect60);

        mButton5.setOnClickListener(this);
        mButton10.setOnClickListener(this);
        mButton15.setOnClickListener(this);
        mButton30.setOnClickListener(this);
        mButton60.setOnClickListener(this);

        mDlg = builder.create();
        return mDlg;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
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
        int minute = 1;
        switch (view.getId()) {
            case R.id.buttonIntervalSelect5:
                minute = 5;
                break;
            case R.id.buttonIntervalSelect10:
                minute = 10;
                break;
            case R.id.buttonIntervalSelect15:
                minute = 15;
                break;
            case R.id.buttonIntervalSelect30:
                minute = 30;
                break;
            case R.id.buttonIntervalSelect60:
                minute = 60;
                break;
        }
        mSettingsActivity.setIntervalNotification(minute * 60000);
        dismiss();
    }
}
