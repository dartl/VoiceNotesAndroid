package com.gawk.voicenotes.adapters.custom_layouts;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by GAWK on 30.10.2017.
 */

public class CustomRelativeLayout extends RelativeLayout {
    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface Listener {
        public void onSoftKeyboardShown(boolean isShowing);
    }
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Activity activity = (Activity) getContext();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int visibleDisplayFrameHeight = rect.top;
        int screenHeight = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        int diff = (screenHeight - visibleDisplayFrameHeight ) - height;
        if (listener != null) {
            listener.onSoftKeyboardShown(diff > 100);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}