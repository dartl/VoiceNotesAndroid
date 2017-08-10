package com.gawk.voicenotes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawk.voicenotes.models.Statistics;
import com.gawk.voicenotes.subs.GooglePlaySubs;
import com.gawk.voicenotes.subs.SubsInterface;

/**
 * Created by GAWK on 10.08.2017.
 */

public class StatisticsActivity extends ParentActivity {
    private TextView mTextViewCreateNotes, mTextViewCreateNotifications, mTextViewGetNotifications,
            mTextViewRemoveNotes, mTextViewExports, mTextViewImports;
    private ImageView mImageViewAvatar;
    private TextView mTextViewRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        mTextViewCreateNotes = (TextView) findViewById(R.id.textViewCreateNotes);
        mTextViewCreateNotifications = (TextView) findViewById(R.id.textViewCreateNotifications);
        mTextViewGetNotifications = (TextView) findViewById(R.id.textViewGetNotifications);
        mTextViewRemoveNotes = (TextView) findViewById(R.id.textViewRemoveNotes);
        mTextViewExports = (TextView) findViewById(R.id.textViewExports);
        mTextViewImports = (TextView) findViewById(R.id.textViewImports);

        mImageViewAvatar = (ImageView) findViewById(R.id.imageViewAvatar);
        mTextViewRank = (TextView) findViewById(R.id.textViewRank);

        Statistics statistics = new Statistics(this);

        mTextViewCreateNotes.setText(String.valueOf(statistics.getCreateNotes()));
        mTextViewCreateNotifications.setText(String.valueOf(statistics.getCreateNotifications()));
        mTextViewGetNotifications.setText(String.valueOf(statistics.getGetNotifications()));
        mTextViewRemoveNotes.setText(String.valueOf(statistics.getRemoveNotes()));
        mTextViewExports.setText(String.valueOf(statistics.getExports()));
        mTextViewImports.setText(String.valueOf(statistics.getImports()));

        checkRank();

        initAdMob(true);
    }

    private void checkRank() {
        Statistics statistics = new Statistics(this);
        if (statistics.getLevel() > 10) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level2);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level2));
        }
        if (statistics.getLevel() > 30) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level3);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level3));
        }
        if (statistics.getLevel() > 50) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level4);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level4));
        }
        if (statistics.getLevel() > 100) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level5);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level5));
        }
        if (statistics.getLevel() > 500) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.level6);
            mImageViewAvatar.setImageBitmap(bm);
            mTextViewRank.setText(getText(R.string.statistics_level6));
        }
    }
}
