package com.gawk.voicenotes.windows;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.NotificationAdapter;

import java.util.ArrayList;

/**
 * Created by GAWK on 26.08.2017.
 */

public class SetNotification extends DialogFragment {
    private Dialog mDlg;
    private View mView;
    private ArrayList<View> allChildren;
    private Switch mSwitchNotification;

    public SetNotification() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        mView = inflater.inflate(R.layout.new_note_notifications, null);
        builder.setView(mView);

        RelativeLayout notificationLayout =  mView.findViewById(R.id.notificationLayout);
        mSwitchNotification = mView.findViewById(R.id.switchNotification);

        allChildren = getAllChildren(notificationLayout);
        for (int i = 0; i < allChildren.size(); i++) {
            allChildren.get(i).setEnabled(true);
        }

        mSwitchNotification.setVisibility(View.GONE);

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

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }
}