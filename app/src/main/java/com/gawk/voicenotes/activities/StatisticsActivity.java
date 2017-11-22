package com.gawk.voicenotes.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawk.voicenotes.R;
import com.gawk.voicenotes.models.Statistics;

/**
 * Created by GAWK on 10.08.2017.
 */

public class StatisticsActivity extends ParentActivity {
    private TextView mTextViewCreateNotes, mTextViewCreateNotifications, mTextViewGetNotifications,
            mTextViewRemoveNotes, mTextViewExports, mTextViewImports, mTextViewLevel, mTextViewExperience;
    private ImageView mImageViewAvatar;
    private TextView mTextViewRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        mTextViewCreateNotes = (TextView) findViewById(R.id.textViewCreateNotes);
        mTextViewCreateNotifications = (TextView) findViewById(R.id.textViewCreateNotifications);
        mTextViewGetNotifications = (TextView) findViewById(R.id.textViewGetNotifications);
        mTextViewRemoveNotes = (TextView) findViewById(R.id.textViewRemoveNotes);
        mTextViewExports = (TextView) findViewById(R.id.textViewExports);
        mTextViewImports = (TextView) findViewById(R.id.textViewImports);
        mTextViewLevel = (TextView) findViewById(R.id.textViewLevel);
        mTextViewExperience = (TextView) findViewById(R.id.textViewExperience);

        mImageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        mTextViewRank = (TextView) findViewById(R.id.textViewRank);

        refresh();

    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_menu);
        navigationView.getMenu().findItem(R.id.menu_statistics).setCheckable(true).setChecked(true);
    }

    private void refresh() {
        Statistics statistics = new Statistics(this);
        mTextViewCreateNotes.setText(String.valueOf(statistics.getCreateNotes()));
        mTextViewCreateNotifications.setText(String.valueOf(statistics.getCreateNotifications()));
        mTextViewGetNotifications.setText(String.valueOf(statistics.getGetNotifications()));
        mTextViewRemoveNotes.setText(String.valueOf(statistics.getRemoveNotes()));
        mTextViewExports.setText(String.valueOf(statistics.getExports()));
        mTextViewImports.setText(String.valueOf(statistics.getImports()));
        mTextViewLevel.setText(getText(R.string.statistics_level_title) + " " + statistics.getLevel());
        mTextViewExperience.setText(String.valueOf(statistics.getExperience()) + "/" +
                String.valueOf(statistics.getBorderExperience() + statistics.getUpExperience()));

        checkRank();
    }

    private void checkRank() {
        Statistics statistics = new Statistics(this);
        if (statistics.getLevel() >= 10) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level2);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level2));
        }
        if (statistics.getLevel() >= 30) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level3);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level3));
        }
        if (statistics.getLevel() >= 50) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level4);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level4));
        }
        if (statistics.getLevel() >= 100) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level5);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level5));
        }
        if (statistics.getLevel() >= 500) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level6);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level6));
        }
    }
}
