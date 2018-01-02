package com.gawk.voicenotes.adapters.lists_adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.fragments.main_activity.CategoryListFragment;
import com.gawk.voicenotes.adapters.ActionsListNotes;
import com.gawk.voicenotes.models.Category;

/**
 * Created by GAWK on 24.10.2017.
 */

public class CategoriesRecyclerAdapter extends CursorRecyclerViewAdapter<CategoriesRecyclerAdapter.ViewHolder> implements View.OnLongClickListener {

    private ActionsListNotes actionsListNotes;
    private CategoryListFragment mCategoryListFragment;
    private Boolean mStateSelected = false;

    public CategoriesRecyclerAdapter(CategoryListFragment context, Cursor cursor, ActionsListNotes actionsListNotes) {
        super(context.getContext(), cursor);
        mCategoryListFragment = context;
        this.actionsListNotes = actionsListNotes;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewCategoryName;
        ImageButton mImageButtonMoreMenu, mImageButtonIconCategory;
        CardView mCardView;
        View parent;
        CategoriesRecyclerAdapter mCategoriesRecyclerAdapter;

        ViewHolder(View v) {
            super(v);
            parent = v;
            mImageButtonMoreMenu = v.findViewById(R.id.imageButtonMoreMenu);
            mImageButtonIconCategory = v.findViewById(R.id.imageButtonIconCategory);
            mCardView = v.findViewById(R.id.card_viewCategory);
            mTextViewCategoryName = v.findViewById(R.id.textViewCategoryName);
        }

        void setData(final Cursor c, final CategoriesRecyclerAdapter categoriesRecyclerAdapter) {
            mCategoriesRecyclerAdapter = categoriesRecyclerAdapter;
            final long id = mCategoriesRecyclerAdapter.getItemId(getLayoutPosition());

            changeItemSelect(mCategoriesRecyclerAdapter.getActionsListNotes().checkSelectElement(id));

            mImageButtonIconCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCategoriesRecyclerAdapter.getActionsListNotes().isStateSelected()) {
                        parent.performLongClick();
                    }
                }
            });

            mImageButtonIconCategory.setImageResource(R.drawable.ic_collections_bookmark_white_24dp);
            mImageButtonIconCategory.setColorFilter(mCategoriesRecyclerAdapter.getColorByAttr(R.attr.primaryColor500));

            if (mCategoriesRecyclerAdapter.getActionsListNotes().isStateSelected()) {
                if (mCategoriesRecyclerAdapter.getActionsListNotes().checkSelectElement(id)) {
                    mImageButtonIconCategory.setImageResource(R.drawable.ic_done_white_24dp);
                    mImageButtonIconCategory.setColorFilter(ContextCompat.getColor(mCategoriesRecyclerAdapter.getContext(), R.color.colorWhite));
                    mImageButtonIconCategory.setBackgroundResource(R.drawable.list_item_circle_primary);
                } else {
                    mImageButtonIconCategory.setImageResource(R.drawable.ic_collections_bookmark_white_24dp);
                    mImageButtonIconCategory.setBackgroundResource(R.drawable.list_item_circle);
                }
                mImageButtonMoreMenu.setVisibility(View.INVISIBLE);
            } else {
                mImageButtonIconCategory.setBackgroundResource(0);
                mImageButtonMoreMenu.setVisibility(View.VISIBLE);
            }

            mImageButtonMoreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCategoriesRecyclerAdapter.getActionsListNotes().showBottomMenu(id);
                }
            });

            parent.setLongClickable(true);
            parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    changeItemSelect(mCategoriesRecyclerAdapter.getActionsListNotes().selectElement(id,getLayoutPosition()));
                    return true;
                }
            });

            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("GAWK_ERR", "select filter = " + id);
                    if (categoriesRecyclerAdapter.getActionsListNotes().isStateSelected()) {
                        parent.performLongClick();
                    } else {
                        Log.e("GAWK_ERR", "select filter = " + id);
                        mCategoriesRecyclerAdapter.mCategoryListFragment.filterNotes(id);
                    }
                }
            });

            Category category = new Category(c);
            mTextViewCategoryName.setText(category.getName());
        }

        private void changeItemSelect(boolean state) {
            if (state) {
                mCardView.setBackgroundColor(mCategoriesRecyclerAdapter.getColorByAttr(R.attr.colorSelectListItem));
            } else {
                mCardView.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.colorWhite));
            }
        }

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public CategoriesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.list_item_category, parent, false);
        return new CategoriesRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CategoriesRecyclerAdapter.ViewHolder viewHolder, Cursor cursor) {
        cursor.moveToPosition(cursor.getPosition());
        viewHolder.setData(cursor, this);
    }

    @Override
    public boolean onLongClick(View view) {
        view.setBackgroundColor(ContextCompat.getColor(getContext(), getColorByAttr(R.attr.primaryColor)));
        return false;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public ActionsListNotes getActionsListNotes() {
        return actionsListNotes;
    }

}