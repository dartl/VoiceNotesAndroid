package com.gawk.voicenotes.fragments_notes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gawk.voicenotes.R;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteNotifications extends Fragment {
    public NewNoteNotifications() {
        // Required empty public constructor
    }

    String data[] = new String[] { "one", "two", "three", "four" };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_note_notifications, null);
    }
}
