package com.gawk.voicenotes.activities.fragments.view_note;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.speech.recognition.ListenerSpeechRecognition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Created by GAWK on 24.10.2017.
 */

public class NoteViewFragment extends FragmentParent {
    private EditText mEditTextNoteText;
    private TextView mTextViewDate, mTextViewCategoryName;
    private FloatingActionButton mImageButton_NewNoteAdd, mImageButton_NewNoteClear;
    private ListenerSpeechRecognition mListenerSpeechRecognition;
    private long id;

    public NoteViewFragment(long id) {
        this.id = id;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_viewnote_fragment_note, null);
        mTextViewDate =  view.findViewById(R.id.textViewDate);
        mTextViewCategoryName = view.findViewById(R.id.textViewCategoryName);
        mEditTextNoteText = view.findViewById(R.id.editTextNoteText);


        mImageButton_NewNoteAdd = view.findViewById(R.id.imageButton_NewNoteAdd);
        mImageButton_NewNoteClear =  view.findViewById(R.id.imageButton_NewNoteClear);

        mImageButton_NewNoteClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditTextNoteText.setText("");
            }
        });

        mImageButton_NewNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecognizeDialog();
            }
        });

        Note note = new Note(dbHelper.getNoteById(id));
        mEditTextNoteText.setText(note.getText_note());

        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance();
        if (mTextViewDate != null) {
            mTextViewDate.setText(dateFormat.format(note.getDate()));
        }

        mTextViewCategoryName.setText(dbHelper.getNameCategory(note.getCategoryId()));

        return view;
    }

    private void showRecognizeDialog() {
        if (mListenerSpeechRecognition == null) mListenerSpeechRecognition = new ListenerSpeechRecognition(getActivity(), mEditTextNoteText);
        mListenerSpeechRecognition.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListenerSpeechRecognition != null) {
            mListenerSpeechRecognition.pause();
        }
    }
}
