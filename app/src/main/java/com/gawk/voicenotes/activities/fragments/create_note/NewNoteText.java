package com.gawk.voicenotes.activities.fragments.create_note;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.ActionsEditedNote;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.CategoriesSpinner;
import com.gawk.voicenotes.activities.fragments.main_activity.adapters.ListenerSelectFilterCategory;
import com.gawk.voicenotes.adapters.custom_layouts.CustomRelativeLayout;
import com.gawk.voicenotes.adapters.logs.CustomLogger;
import com.gawk.voicenotes.adapters.preferences.PrefUtil;
import com.gawk.voicenotes.adapters.speech_recognition.ListenerSpeechRecognition;
import com.gawk.voicenotes.models.Note;

import java.util.Calendar;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends FragmentParent implements CustomRelativeLayout.Listener, ListenerSelectFilterCategory{
    private EditText editText_NewNoteText;
    private Spinner mSpinnerSelectCategory;
    private ListenerSpeechRecognition mListenerSpeechRecognition;
    private CategoriesSpinner mCategoriesSpinner;
    private Note mCurrentNote;
    private PrefUtil mPrefUtil;

    public NewNoteText() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_note_fragment_note, null);
        mPrefUtil = new PrefUtil(getContext());

        editText_NewNoteText = view.findViewById(R.id.editText_NewNoteText);
        ImageButton mImageButton_NewNoteAdd = view.findViewById(R.id.imageButton_NewNoteAdd);
        ImageButton mImageButton_NewNoteClear = view.findViewById(R.id.imageButton_NewNoteClear);
        ImageButton mImageButton_NewNoteEnter = view.findViewById(R.id.imageButton_NewNoteEnter);
        Button mButton_NewNoteEdited = view.findViewById(R.id.button_NewNoteEdited);
        mSpinnerSelectCategory = view.findViewById(R.id.spinnerSelectCategory);

        mCategoriesSpinner = new CategoriesSpinner(dbHelper, getContext(), mSpinnerSelectCategory);
        mCategoriesSpinner.setListenerSelectFilterCategory(this, false);

        ActionsEditedNote actionsEditedNote = new ActionsEditedNote(mImageButton_NewNoteClear, mImageButton_NewNoteEnter,
                mButton_NewNoteEdited, editText_NewNoteText, getContext());
        actionsEditedNote.init();

        mImageButton_NewNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation_create_note_click_button));
                showRecognizeDialog();
            }
        });

        editText_NewNoteText.addTextChangedListener(new TextWatcher() {
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

        showRecognizeDialog();
        return view;
    }

    private void showRecognizeDialog() {
        if (mListenerSpeechRecognition == null) mListenerSpeechRecognition = new ListenerSpeechRecognition(getActivity(), editText_NewNoteText);
        mListenerSpeechRecognition.show();
    }

    private String getTextNote() {
        if (editText_NewNoteText != null) {
            return String.valueOf(editText_NewNoteText.getText());
        }
        return "";
    }

    private long getSelectedCategoryId() {
        return mCategoriesSpinner.getSelectedCategoryId();
    }

    public Note getUpdateNote() {
        if (mCurrentNote == null) return new Note();
        return mCurrentNote;
    }

    public void saveNote() {
        if (mCurrentNote == null) {
            mCurrentNote = new Note(-1,getTextNote(), Calendar.getInstance().getTime(),getSelectedCategoryId());
        } else {
            mCurrentNote.setDate(Calendar.getInstance().getTime());
            mCurrentNote.setCategoryId(getSelectedCategoryId());
            mCurrentNote.setText_note(getTextNote());
        }
        if (mPrefUtil.getBoolean(PrefUtil.NOTE_AUTO_SAVE, false)) {
            long note_id = dbHelper.saveNote(mCurrentNote);
            mCurrentNote.setId(note_id);
        }
    }

    @Override
    public void onSoftKeyboardShown(boolean isShowing) {
        if(mSpinnerSelectCategory == null) return;
        if(isShowing) {
            mSpinnerSelectCategory.setVisibility(View.GONE);
        } else {
            mSpinnerSelectCategory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void changeCategoryFilter(long newCategoryId) {
        saveNote();
    }
}
