package com.gawk.voicenotes.adapters.lists_adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.activities.SettingsActivity;

/**
 * Created by GAWK on 23.10.2017.
 */

public class ListThemeAdapter extends RecyclerView.Adapter<ListThemeAdapter.ViewHolder> {

    int[] mDataset;
    final SettingsActivity mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        Button mButton;
        ViewHolder(Button v) {
            super(v);
            mButton = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListThemeAdapter(SettingsActivity contexts, int[] myDataset) {
        mDataset = myDataset;
        mContext = contexts;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListThemeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_select_theme, parent, false);
        return new ViewHolder((Button) v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mButton.setText(mContext.getText(R.string.app_name));
        holder.mButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        holder.mButton.setBackgroundColor(ContextCompat.getColor(mContext, mDataset[position]));
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.changeTheme(mDataset[position]);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}