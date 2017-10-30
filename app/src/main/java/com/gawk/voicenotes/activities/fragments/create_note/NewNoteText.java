package com.gawk.voicenotes.activities.fragments.create_note;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.ActionsEditedNote;
import com.gawk.voicenotes.activities.fragments.create_note.adapters.CategoriesSpinner;
import com.gawk.voicenotes.adapters.custom_layouts.CustomRelativeLayout;
import com.gawk.voicenotes.adapters.speech_recognition.ListenerSpeechRecognition;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends FragmentParent implements CustomRelativeLayout.Listener{
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

        ActionsEditedNote actionsEditedNote = new ActionsEditedNote(mImageButton_NewNoteClear, mButton_NewNoteEdited, editText_NewNoteText, getContext());
        actionsEditedNote.init();

        mImageButton_NewNoteAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.animation_create_note_click_button));
                showRecognizeDialog();
            }
        });

        editText_NewNoteText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.e("GAWK_ERR","click editor");
                return false;
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

    @Override
    public void onSoftKeyboardShown(boolean isShowing) {
        if(mSpinnerSelectCategory == null) return;
        Log.e("GAWK_ERR","onSoftKeyboardShown isShowing = " + isShowing);
        if(isShowing) {
            mSpinnerSelectCategory.setVisibility(View.GONE);
        } else {
            mSpinnerSelectCategory.setVisibility(View.VISIBLE);
        }
    }
}
