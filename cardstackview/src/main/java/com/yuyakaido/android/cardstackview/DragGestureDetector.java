package com.yuyakaido.android.cardstackview;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class DragGestureDetector {
    private GestureDetectorCompat gestureDetector;
    private DragListener dragListener;
    private MotionEvent prevMotionEvent;
    private boolean isDragging;

    public interface DragListener {
        void onBeginDrag(MotionEvent e1, MotionEvent e2);
        void onDragging(MotionEvent e1, MotionEvent e2);
        void onEndDrag(MotionEvent e1, MotionEvent e2);
        void onTapUp();
    }

    public DragGestureDetector(Context context, DragListener dragListener) {
        this.gestureDetector = new GestureDetectorCompat(context, new InternalGestureListener());
        this.dragListener = dragListener;
    }

    public void onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    dragListener.onEndDrag(prevMotionEvent, event);
                }
                isDragging = false;
                break;
            case MotionEvent.ACTION_DOWN:
                prevMotionEvent = event;
                break;
        }
    }

    private class InternalGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isDragging) {
                dragListener.onDragging(e1, e2);
            } else {
                dragListener.onBeginDrag(e1, e2);
                isDragging = true;
            }
            prevMotionEvent = e1;
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            dragListener.onTapUp();
            return true;
        }

    }

}
