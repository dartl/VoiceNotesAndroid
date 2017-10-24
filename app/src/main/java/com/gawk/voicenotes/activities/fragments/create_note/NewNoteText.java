package com.gawk.voicenotes.activities.fragments.create_note;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.gawk.voicenotes.activities.fragments.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.models.Category;
import com.gawk.voicenotes.speech.recognition.ListenerSpeechRecognition;

import java.util.ArrayList;

/**
 * Created by GAWK on 12.02.2017.
 */

public class NewNoteText extends FragmentParent{
    private EditText editText_NewNoteText;
    private Spinner mSpinnerSelectCategory;
    private FloatingActionButton imageButton_NewNoteAdd, imageButton_NewNoteClear;
    private ListenerSpeechRecognition mListenerSpeechRecognition;
    private ArrayList<Category> mCategories = new ArrayList<>();
    private String[] mCategoriesNames;
    private int selectedCategoryId;

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

        upDateCategoriesArray();
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, mCategoriesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSelectCategory.setAdapter(adapter);

        mSpinnerSelectCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    selectedCategoryId = -1;
                } else {
                    selectedCategoryId = getCategoryIdByName(mCategoriesNames[position]);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

            private int getCategoryIdByName(String name) {
                for(int i = 0; i < mCategories.size(); i++) {
                    if (mCategories.get(i).getName().equals(name))
                        return (int) mCategories.get(i).getId();
                }
                return -1;
            }
        });

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

    private void upDateCategoriesArray() {
        Cursor categories = dbHelper.getCursorAllCategories();
        Category category;
        mCategoriesNames = new String[categories.getCount()+1];
        mCategoriesNames[0] = getString(R.string.main_unassigned);
        for (int i = 0; i < categories.getCount(); i++) {
            categories.moveToPosition(i);
            category = new Category(categories);
            mCategories.add(category);
            mCategoriesNames[i+1] = category.getName();
        }
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

    public int getSelectedCategoryId() {
        return selectedCategoryId;
    }

}
