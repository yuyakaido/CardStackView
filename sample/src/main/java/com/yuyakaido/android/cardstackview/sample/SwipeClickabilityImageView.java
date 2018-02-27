package com.yuyakaido.android.cardstackview.sample;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by luongvo on 2/23/18.
 */

public class SwipeClickabilityImageView extends ImageView {

    private static final int THRESHOLD = 5;
    private float x, y;
    private boolean cancelOnClick;

    public SwipeClickabilityImageView(Context context) {
        super(context);
    }

    public SwipeClickabilityImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeClickabilityImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                cancelOnClick = false;
                break;
            case MotionEvent.ACTION_MOVE:
                // disable on click listener when moving out of threshold
                if (Math.abs(x - event.getX()) > THRESHOLD || Math.abs(y - event.getY()) > THRESHOLD) {
                    cancelOnClick = true;
                }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return cancelOnClick || super.performClick();
    }
}
