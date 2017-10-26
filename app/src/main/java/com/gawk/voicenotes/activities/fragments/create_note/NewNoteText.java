package com.gawk.voicenotes.activities.fragments.create_note;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.ActionsEditedNote;
import com.gawk.voicenotes.createnote.CategoriesSpinner;
import com.gawk.voicenotes.adapters.speech_recognition.ListenerSpeechRecognition;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends FragmentParent{
    private EditText editText_NewNoteText;
    private Spinner mSpinnerSelectCategory;
    private ImageButton mImageButton_NewNoteAdd, mImageButton_NewNoteClear;
    private Button mButton_NewNoteEdited;
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
        mImageButton_NewNoteAdd = view.findViewById(R.id.imageButton_NewNoteAdd);
        mImageButton_NewNoteClear =  view.findViewById(R.id.imageButton_NewNoteClear);
        mButton_NewNoteEdited = view.findViewById(R.id.button_NewNoteEdited);
        mSpinnerSelectCategory = view.findViewById(R.id.spinnerSelectCategory);


        mCategoriesSpinner = new CategoriesSpinner(dbHelper, getContext(), mSpinnerSelectCategory);

        ActionsEditedNote actionsEditedNote = new ActionsEditedNote(mImageButton_NewNoteClear, mButton_NewNoteEdited, editText_NewNoteText);
        actionsEditedNote.init();

        mImageButton_NewNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecognizeDialog();
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
