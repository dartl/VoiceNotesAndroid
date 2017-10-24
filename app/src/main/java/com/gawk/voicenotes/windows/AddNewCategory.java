package com.gawk.voicenotes.windows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.fragments_main.CategoryListFragment;
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

        View view = inflater.inflate(R.layout.add_category_dialog, null);

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
    }
}