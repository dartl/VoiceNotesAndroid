package com.gawk.voicenotes.lists_adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.gawk.voicenotes.FragmentParent;
import com.gawk.voicenotes.R;
import com.gawk.voicenotes.adapters.ActionMenuBottom;
import com.gawk.voicenotes.adapters.ActionsListNotes;

import java.util.ArrayList;

/**
 * Created by GAWK on 28.08.2017.
 */

public class ListAdapters implements ActionsListNotes {
    private ArrayList mSelectItems = new ArrayList<>();
    private long mId_item;
    private AppCompatImageView mImageButtonDelete, mImageButtonShare;
    private TextView mTextViewNoteSelectCount;
    private RelativeLayout mRelativeLayoutBottomMenu;
    private BottomSheet mBottomMenu;
    private boolean mStateSelected;

    private View mParentView;
    private ActionMenuBottom mActionMenuBottom;
    private Activity mActivity;

    public ListAdapters(View mParentView, ActionMenuBottom mActionMenuBottom, Activity mActivity) {
        this.mParentView = mParentView;
        this.mActionMenuBottom = mActionMenuBottom;
        this.mActivity = mActivity;
        init();
    }

    private void init() {
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
                mActionMenuBottom.shareItemList(-1,mSelectItems);
                mSelectItems.clear();
                changeBottomMenu();
            }
        });

        mBottomMenu = new BottomSheet.Builder(mActivity).title(mActivity.getResources()
                .getText(R.string.main_action_element))
                .sheet(R.menu.menu_list_actions)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.action_remove_element:
                                showDialogDelete(mId_item);
                                break;
                            case R.id.action_share_element:
                                mActionMenuBottom.shareItemList(mId_item,mSelectItems);
                                break;
                            case R.id.action_edited_element:
                                mActionMenuBottom.editedItemList(mId_item);
                                break;
                        }
                    }
                }).build();
    }


    public void showDialogDelete(final long _id) {
        if (mSelectItems.size() > 0|| _id != -1 ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.dialogDeleteMessage)
                    .setTitle(R.string.dialogDeleteTitle);

            // Add the buttons
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mActionMenuBottom.deleteItemList(_id,false,mSelectItems);
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
            Snackbar.make(mParentView, mActivity.getResources().getString(R.string.main_view_error_select), Snackbar.LENGTH_LONG).show();
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
        mId_item = id;
        mBottomMenu.show();
    }

    public void changeVisibleItemMenu(int id, boolean visible) {
        mBottomMenu.getMenu().findItem(id).setVisible(visible);
    }

    public void changeVisibleItemSelectedMenu(int id, int visible) {
        mParentView.findViewById(id).setVisibility(visible);
    }

    private void changeBottomMenu() {
        mActionMenuBottom.refreshSelectedList();
        mTextViewNoteSelectCount.setText(mSelectItems.size() + " " + mActivity.getResources().getText(R.string.main_selected_element));
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
