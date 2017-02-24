package com.gawk.voicenotes.fragments_notes;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gawk.voicenotes.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends Fragment {
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
        startRecognize();
        return view;
    }

    private void startRecognize() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
        Log.e("GAWK_ERR", String.valueOf(Locale.getDefault()));
        try {
            getActivity().startActivityForResult(i, getResources().getInteger(R.integer.constant_request_code_recognize));
        } catch (Exception e) {
            Toast.makeText(this.getContext(), "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
            Log.e("GAWK_ERR", e.getMessage());
        }
    }

    public void setActivityResult(Intent data) {
        ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        editText_NewNoteText.setText(editText_NewNoteText.getText()+ "" +thingsYouSaid.get(0));
    }

    public String getTextNote() {
        return String.valueOf(editText_NewNoteText.getText());
    }
}
