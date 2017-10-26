package com.gawk.voicenotes.activities.fragments.create_note;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.createnote.CategoriesSpinner;
import com.gawk.voicenotes.adapters.speech_recognition.ListenerSpeechRecognition;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends FragmentParent{
    private EditText editText_NewNoteText;
    private Spinner mSpinnerSelectCategory;
    private FloatingActionButton imageButton_NewNoteAdd, imageButton_NewNoteClear;
    private ListenerSpeechRecognition mListenerSpeechRecognition;
    private CategoriesSpinner mCategoriesSpinner;

    public NewNoteText() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_note_fragment_note, null);
        editText_NewNoteText = view.findViewById(R.id.editText_NewNoteText);
        imageButton_NewNoteAdd = view.findViewById(R.id.imageButton_NewNoteAdd);
        imageButton_NewNoteClear =  view.findViewById(R.id.imageButton_NewNoteClear);
        mSpinnerSelectCategory = view.findViewById(R.id.spinnerSelectCategory);

        mCategoriesSpinner = new CategoriesSpinner(dbHelper, getContext(), mSpinnerSelectCategory);

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
                mListenerSpeechRecognition.clearSuggestions();
            }
        });

        showRecognizeDialog();
        return view;
    }

    private void showRecognizeDialog() {
        if (mListenerSpeechRecognition == null) mListenerSpeechRecognition = new ListenerSpeechRecognition(getActivity(), editText_NewNoteText);
        mListenerSpeechRecognition.show();
    }

    public String getTextNote() {
        if (editText_NewNoteText != null) {
            return String.valueOf(editText_NewNoteText.getText());
        }
        return "";
    }

    public long getSelectedCategoryId() {
        return mCategoriesSpinner.getSelectedCategoryId();
    }

}
