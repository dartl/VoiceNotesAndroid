package com.gawk.voicenotes.windows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.SettingsActivity;
import com.gawk.voicenotes.adapters.lists_adapters.ListTextSizeAdapter;
import com.gawk.voicenotes.adapters.lists_adapters.ListThemeAdapter;

/**
 * Created by GAWK on 23.10.2017.
 */

public class SelectTextSize extends DialogFragment {
    private Dialog mDlg;
    private Button mButtonCancel;
    private RecyclerView mRecyclerViewSelectTheme;
    private RecyclerView.LayoutManager mLayoutManager;
    int[] values = new int[] {
            14,
            18,
            22,
            32,
            40
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_settings_select_theme, null);
        mRecyclerViewSelectTheme = view.findViewById(R.id.recyclerViewSelectTheme);
        mButtonCancel = view.findViewById(R.id.buttonCancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewSelectTheme.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewSelectTheme.setLayoutManager(mLayoutManager);

        mRecyclerViewSelectTheme.setAdapter(new ListTextSizeAdapter((SettingsActivity) getActivity(),values));
        builder.setView(view);

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
}
