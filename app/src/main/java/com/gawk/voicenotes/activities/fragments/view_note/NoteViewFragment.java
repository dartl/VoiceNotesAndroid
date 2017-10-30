package com.gawk.voicenotes.activities.fragments.view_note;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.ActionsEditedNote;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.CategoriesSpinner;
import com.gawk.voicenotes.activities.fragments.main_activity.adapters.ListenerSelectFilterCategory;
import com.gawk.voicenotes.adapters.custom_layouts.CustomRelativeLayout;
import com.gawk.voicenotes.adapters.preferences.PrefUtil;
import com.gawk.voicenotes.models.Note;
import com.gawk.voicenotes.adapters.speech_recognition.ListenerSpeechRecognition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by GAWK on 24.10.2017.
 */

public class NoteViewFragment extends FragmentParent implements CustomRelativeLayout.Listener, ListenerSelectFilterCategory {
    private EditText mEditTextNoteText;
    private TextView mTextViewDate;
    private ImageButton mImageButton_NewNoteAdd, mImageButton_NewNoteClear;
    private Button mButton_NewNoteEdited;
    private Spinner mSpinnerSelectCategory;
    private Note mNote;
    private long id;
    private PrefUtil mPrefUtil;

    private ListenerSpeechRecognition mListenerSpeechRecognition;
    private CategoriesSpinner mCategoriesSpinner;

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
        mPrefUtil = new PrefUtil(getContext());

        mTextViewDate =  view.findViewById(R.id.textViewDate);
        mEditTextNoteText = view.findViewById(R.id.editTextNoteText);
        mSpinnerSelectCategory = view.findViewById(R.id.spinnerSelectCategory);

        mNote = new Note(dbHelper.getNoteById(id));

        mCategoriesSpinner = new CategoriesSpinner(dbHelper, getContext(), mSpinnerSelectCategory, mNote.getCategoryId());
        mCategoriesSpinner.setListenerSelectFilterCategory(this, false);

        mImageButton_NewNoteAdd = view.findViewById(R.id.imageButton_NewNoteAdd);
        mImageButton_NewNoteClear =  view.findViewById(R.id.imageButton_NewNoteClear);
        mButton_NewNoteEdited =  view.findViewById(R.id.button_NewNoteEdited);

        mImageButton_NewNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation_create_note_click_button));
                showRecognizeDialog();
            }
        });

        ActionsEditedNote actionsEditedNote = new ActionsEditedNote(mImageButton_NewNoteClear,
                mButton_NewNoteEdited, mEditTextNoteText, getContext());
        actionsEditedNote.init();

        mEditTextNoteText.setText(mNote.getText_note());

        DateFormat dateFormat;
        dateFormat = SimpleDateFormat.getDateTimeInstance();
        if (mTextViewDate != null) {
            mTextViewDate.setText(dateFormat.format(mNote.getDate()));
        }

        mEditTextNoteText.addTextChangedListener(new TextWatcher() {
            private int mCount = 0;

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (mCount + count > 3) mCount = 0; saveNote();
                mCount += count;
            }
        });

        return view;
    }

    public void saveNote() {
        mNote.setCategoryId(getSelectedCategoryId());
        mNote.setText_note(getTextNote());
        if (mPrefUtil.getBoolean(PrefUtil.NOTE_AUTO_SAVE, false)) dbHelper.saveNote(mNote);
    }

    public String getTextNote() {
        if (mEditTextNoteText != null) {
            return String.valueOf(mEditTextNoteText.getText());
        }
        return "";
    }

    public long getSelectedCategoryId() {
        return mCategoriesSpinner.getSelectedCategoryId();
    }

    private void showRecognizeDialog() {
        if (mListenerSpeechRecognition == null) mListenerSpeechRecognition = new ListenerSpeechRecognition(getActivity(), mEditTextNoteText);
        mListenerSpeechRecognition.show();
    }

    public Note getUpdateNote() {
        return mNote;
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

    @Override
    public void onSoftKeyboardShown(boolean isShowing) {
        if(mTextViewDate == null || mSpinnerSelectCategory == null) {
            Log.e("GAWK_ERR","onSoftKeyboardShown NULL");
            return;
        }
        if(isShowing) {
            mTextViewDate.setVisibility(View.GONE);
            mSpinnerSelectCategory.setVisibility(View.GONE);
        } else {
            mTextViewDate.setVisibility(View.VISIBLE);
            mSpinnerSelectCategory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void changeCategoryFilter(long newCategoryId) {
        saveNote();
    }
}
