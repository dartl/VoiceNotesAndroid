package com.gawk.voicenotes.activities.fragments.create_note.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by GAWK on 26.10.2017.
 */

public class ActionsEditedNote {
    private ImageButton mImageButton_NewNoteClear;
    private Button mButton_NewNoteEdited;
    private EditText mEditText;

    public ActionsEditedNote(ImageButton mImageButton_NewNoteClear, Button mButton_NewNoteEdited, EditText mEditText) {
        this.mImageButton_NewNoteClear = mImageButton_NewNoteClear;
        this.mButton_NewNoteEdited = mButton_NewNoteEdited;
        this.mEditText = mEditText;
    }

    public void init() {
        mImageButton_NewNoteClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int length = mEditText.getText().length();
                if (length > 0) {
                    mEditText.getText().delete(length - 1, length);
                }
                mEditText.setSelection(mEditText.getText().length());
            }
        });

        mButton_NewNoteEdited.setOnClickListener(new View.OnClickListener() {
            private long mLastClickButton_NewNoteEdited = 0;
            private int state = 0;

            @Override
            public void onClick(View view) {
                long currentTime = System.currentTimeMillis();
                String str = mEditText.getText().toString();
                if (currentTime - mLastClickButton_NewNoteEdited > 1000) {
                    state = 0;
                    str = str.trim();
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
}
