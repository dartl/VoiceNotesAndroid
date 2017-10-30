package com.gawk.voicenotes.activities.fragments.create_note.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.main_activity.adapters.ListenerSelectFilterCategory;
import com.gawk.voicenotes.adapters.SQLiteDBHelper;
import com.gawk.voicenotes.adapters.speech_recognition.ListenerSpeechRecognition;
import com.gawk.voicenotes.models.Category;

import java.util.ArrayList;

/**
 * Created by GAWK on 25.10.2017.
 */

public class CategoriesSpinner {
    private SQLiteDBHelper dbHelper;
    private Context mContext;
    private Spinner mSpinnerSelectCategory;
    private ArrayList<Category> mCategories = new ArrayList<>();
    private String[] mCategoriesNames;
    private long selectedCategoryId = -1;
    private int positionSelectedCategory = 0;
    private ListenerSelectFilterCategory mListenerSelectFilterCategory;
    private ArrayAdapter mAdapter;

    public CategoriesSpinner(SQLiteDBHelper dbHelper, Context mContext, Spinner mSpinner) {
        this.dbHelper = dbHelper;
        this.mContext = mContext;
        this.mSpinnerSelectCategory = mSpinner;
        init();
    }

    public CategoriesSpinner(SQLiteDBHelper dbHelper, Context mContext, Spinner mSpinnerSelectCategory, long selectedCategoryId) {
        this.dbHelper = dbHelper;
        this.mContext = mContext;
        this.mSpinnerSelectCategory = mSpinnerSelectCategory;
        this.selectedCategoryId = selectedCategoryId;
        init();
    }

    private void init() {
        upDateCategoriesArray();

        mAdapter = new ArrayAdapter(mContext, R.layout.simple_spinner_item, mCategoriesNames);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSelectCategory.setAdapter(mAdapter);
        mSpinnerSelectCategory.setSelection(positionSelectedCategory);
        mSpinnerSelectCategory.setPrompt(mContext.getText(R.string.category_spinner_title));

        mSpinnerSelectCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    selectedCategoryId = -1;
                } else {
                    selectedCategoryId = getCategoryIdByName(mCategoriesNames[position]);
                }
                if (mListenerSelectFilterCategory != null) {
                    mListenerSelectFilterCategory.changeCategoryFilter(selectedCategoryId);
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
    }

    private void upDateCategoriesArray() {
        Cursor categories = dbHelper.getCursorAllCategories();
        Category category;
        mCategoriesNames = new String[categories.getCount()+1];
        mCategoriesNames[0] = mContext.getString(R.string.main_unassigned);
        for (int i = 0; i < categories.getCount(); i++) {
            categories.moveToPosition(i);
            category = new Category(categories);
            mCategories.add(category);
            mCategoriesNames[i+1] = category.getName();
            if (category.getId() == selectedCategoryId) {
                positionSelectedCategory = i + 1;
            }
        }
    }

    public long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setListenerSelectFilterCategory(ListenerSelectFilterCategory
                                                        mListenerSelectFilterCategory,
                                                boolean isFilter) {
        if (isFilter) mCategoriesNames[0] = mContext.getString(R.string.main_all);
        this.mListenerSelectFilterCategory = mListenerSelectFilterCategory;
    }

    public void refresh() {
        mSpinnerSelectCategory.setAdapter(mAdapter);
    }
}
