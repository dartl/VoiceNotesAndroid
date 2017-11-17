package com.gawk.voicenotes.windows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.main_activity.CategoryListFragment;
import com.gawk.voicenotes.models.Category;

/**
 * Created by GAWK on 24.10.2017.
 */

public class AddNewCategory extends DialogFragment {
    private Dialog mDlg;
    private Button mButtonSave, mButtonCancel;
    private EditText mEditTextNewCategory;
    private Category mCategory;
    private CategoryListFragment mCategoryListFragment;
    private int mAction = 0;
    private InputMethodManager mInputMethodManager;

    public AddNewCategory() {
    }

    public AddNewCategory(CategoryListFragment categoryListFragment) {
        this.mCategoryListFragment = categoryListFragment;
        mCategory = new Category();
        mAction = 0;
    }

    public AddNewCategory(Category category, CategoryListFragment categoryListFragment) {
        this.mCategory = category;
        this.mCategoryListFragment = categoryListFragment;
        mAction = 1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = inflater.inflate(R.layout.dialog_add_category, null);

        mButtonSave = view.findViewById(R.id.buttonSave);
        mButtonCancel = view.findViewById(R.id.buttonCancel);
        mEditTextNewCategory = view.findViewById(R.id.editTextNewCategory);

        builder.setView(view);

        if (mCategory != null) {
            mEditTextNewCategory.setText(mCategory.getName());
        } else {
            mEditTextNewCategory.setHint(R.string.dialog_add_category_hint);
        }

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory.setName(mEditTextNewCategory.getText().toString());
                mCategoryListFragment.saveCategory(mCategory, mAction);
                dismiss();
            }
        });

        mEditTextNewCategory.requestFocus();

        if (mInputMethodManager != null) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

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
        if (mInputMethodManager != null) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
}