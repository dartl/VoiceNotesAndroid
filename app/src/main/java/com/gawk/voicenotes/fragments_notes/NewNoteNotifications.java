package com.gawk.voicenotes.fragments_notes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.SetNotificationFullDialogFragment;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteNotifications extends Fragment {
    private ImageButton newNoteAddNotification;

    public NewNoteNotifications() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_note_notifications, null);
        newNoteAddNotification = (ImageButton) view.findViewById(R.id.imageButton_newNoteAddNotification);
        newNoteAddNotification.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog();

            }
        });
        return view;
    }

    public void showDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        SetNotificationFullDialogFragment newFragment = new SetNotificationFullDialogFragment();

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, newFragment)
                .addToBackStack(null).commit();
    }
}
