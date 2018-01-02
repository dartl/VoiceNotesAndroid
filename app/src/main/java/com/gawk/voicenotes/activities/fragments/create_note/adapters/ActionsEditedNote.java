package com.gawk.voicenotes.activities.fragments.create_note.adapters;

import android.app.Instrumentation;
import android.content.Context;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.gawk.voicenotes.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by GAWK on 26.10.2017.
 */

public class ActionsEditedNote {
    private ImageButton mImageButton_NewNoteClear, mImageButton_NewNoteEnter;
    private Button mButton_NewNoteEdited;
    private EditText mEditText;
    private Context mContext;

    public ActionsEditedNote(ImageButton mImageButton_NewNoteClear, ImageButton mImageButton_NewNoteEnter, Button mButton_NewNoteEdited, EditText mEditText, Context mContext) {
        this.mImageButton_NewNoteClear = mImageButton_NewNoteClear;
        this.mImageButton_NewNoteEnter = mImageButton_NewNoteEnter;
        this.mButton_NewNoteEdited = mButton_NewNoteEdited;
        this.mEditText = mEditText;
        this.mContext = mContext;
    }

    public void init() {
        mImageButton_NewNoteClear.setOnTouchListener(new View.OnTouchListener() {
            TaskClear mTaskClear = new TaskClear();

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(mTaskClear.getStatus() == AsyncTask.Status.PENDING){
                            if (!mEditText.getText().equals("")) mTaskClear.execute(mEditText.getText().toString());
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mTaskClear.cancel(true);
                        mTaskClear = new TaskClear();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        mImageButton_NewNoteEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditText.setText(mEditText.getText() + "\n");
                mEditText.setSelection(mEditText.getText().length());
            }
        });

        mButton_NewNoteEdited.setOnClickListener(new View.OnClickListener() {
            private long mLastClickButton_NewNoteEdited = 0;
            private int state = 0;

            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.animation_create_note_click_button));
                long currentTime = System.currentTimeMillis();
                String str = mEditText.getText().toString();
                if (currentTime - mLastClickButton_NewNoteEdited > 1000) {
                    state = 0;
                    //str = str.trim();
                    str += ".";
                } else {
                    state++;
                    str = str.substring(0,str.length() - 1);
                    switch (state) {
                        case 0:
                            str += ".";
                            break;
                        case 1:
                            str += " ";
                            break;
                        case 2:
                            str += ",";
                            break;
                        case 3:
                            str += "?";
                            break;
                        case 4:
                            str += "!";
                            state = -1;
                            break;
                    }

                }
                mEditText.setText(str);
                mEditText.setSelection(mEditText.getText().length());
                mLastClickButton_NewNoteEdited = currentTime;
            }
        });
    }

    class TaskClear extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String str = params[0];
            long milliseconds = 400;
            try {
                while (true) {
                    isCancelled();
                    int length = str.length();
                    if (length > 0) {
                        str = str.substring(0,length-1);
                        publishProgress(str);
                    } else {
                        cancel(true);
                    }
                    TimeUnit.MILLISECONDS.sleep(milliseconds);
                    if (milliseconds >= 99) milliseconds -= 50;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mEditText.setText(values[0]);
            mEditText.setSelection(mEditText.getText().length());
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
