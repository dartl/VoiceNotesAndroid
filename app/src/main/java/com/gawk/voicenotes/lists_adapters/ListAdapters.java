package com.gawk.voicenotes.lists_adapters;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionsListNotes;

import java.util.ArrayList;

/**
 * Created by GAWK on 28.08.2017.
 */

public class ListAdapters implements ActionsListNotes {
    private ArrayList mSelectItems = new ArrayList<>();
    private ImageButton mImageButtonDelete, mImageButtonShare;
    private TextView mTextViewNoteSelectCount;
    private RelativeLayout mRelativeLayoutBottomMenu;
    private BottomSheet mBottomMenu;
    private boolean mStateSelected;

    private View mParentView;
    private FragmentParent mFragmentParent;

    public ListAdapters(View mParent, FragmentParent mContext) {
        this.mParentView = mParent;
        this.mFragmentParent = mContext;

        mRelativeLayoutBottomMenu = mParentView.findViewById(R.id.relativeLayoutBottomMenu);

        mTextViewNoteSelectCount = mParentView.findViewById(R.id.textViewNoteSelectCount);
        mImageButtonDelete = mParentView.findViewById(R.id.imageButtonDelete);
        mImageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDelete(-1);
            }
        });

        mImageButtonShare =  mParentView.findViewById(R.id.imageButtonShare);
        mImageButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentParent.shareItemList(-1,mSelectItems);
                mSelectItems.clear();
                changeBottomMenu();
            }
        });
    }

    public void showDialogDelete(final long _id) {
        if (mSelectItems.size() > 0|| _id != -1 ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentParent.getContext());
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.dialogDeleteMessage)
                    .setTitle(R.string.dialogDeleteTitle);

            // Add the buttons
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mFragmentParent.deleteItemList(_id,false,mSelectItems);
                    mSelectItems.clear();
                    changeBottomMenu();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            Snackbar.make(mParentView, mFragmentParent.getResources().getString(R.string.main_view_error_select), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean selectElement(long id) {
        if (mSelectItems.contains(id)) {
            mSelectItems.remove(id);
            changeBottomMenu();
            return false;
        } else {
            mSelectItems.add(id);
            changeBottomMenu();
            return true;
        }
    }

    @Override
    public boolean checkSelectElement(long id) {
        return mSelectItems.contains(id);
    }

    @Override
    public void showBottomMenu(final long id) {
        mBottomMenu = new BottomSheet.Builder(mFragmentParent.getActivity()).title(mFragmentParent.getResources()
                .getText(R.string.main_action_element))
                .sheet(R.menu.menu_list_actions)
                .listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.action_remove_element:
                        showDialogDelete(id);
                        break;
                }
            }
        }).show();
    }

    private void changeBottomMenu() {
        mFragmentParent.refreshSelectedList();
        mTextViewNoteSelectCount.setText(mSelectItems.size() + " " + mFragmentParent.getResources().getText(R.string.main_selected_element));
        if (mSelectItems.size() > 0) {
            mStateSelected = true;
            mRelativeLayoutBottomMenu.getLayoutParams().height = dpToPx(64);
            mRelativeLayoutBottomMenu.requestLayout();
            mRelativeLayoutBottomMenu.animate().translationY(0);
        } else {
            mStateSelected = false;
            mRelativeLayoutBottomMenu.animate().translationY(mRelativeLayoutBottomMenu.getHeight());
            mRelativeLayoutBottomMenu.animate().withEndAction(new Runnable() {
                @Override
                public void run() {
                    mRelativeLayoutBottomMenu.getLayoutParams().height= 0;
                    mRelativeLayoutBottomMenu.requestLayout();
                }
            });
        }
    }

    @Override
    public boolean isStateSelected() {
        return mStateSelected;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public void setStateSelected(Boolean mStateSelected) {
        this.mStateSelected = mStateSelected;
    }
}
