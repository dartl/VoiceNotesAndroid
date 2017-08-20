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
import android.widget.RatingBar;
import android.widget.TextView;

import com.gawk.voicenotes.R;

import org.w3c.dom.Text;

/**
 * Created by GAWK on 17.08.2017.
 */

public class VotesDialog extends DialogFragment {
    private Dialog mDlg;
    private RatingBar mRatingBarVotes;
    private Button mButtonWriteUs, mButtonCancel;
    private TextView mTextViewWriteUs;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.votes_dialog, null);

        mRatingBarVotes = (RatingBar) view.findViewById(R.id.ratingBarVotes);

        mButtonWriteUs = (Button) view.findViewById(R.id.buttonWriteUs);
        mButtonCancel = (Button) view.findViewById(R.id.buttonCancel);

        mTextViewWriteUs = (TextView) view.findViewById(R.id.textViewWriteUs);

        builder.setView(view);

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mRatingBarVotes.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating >= 4) {

                } else {
                    mRatingBarVotes.setVisibility(View.GONE);
                    mTextViewWriteUs.setVisibility(View.VISIBLE);
                    mButtonWriteUs.setVisibility(View.VISIBLE);
                }
            }
        });

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
