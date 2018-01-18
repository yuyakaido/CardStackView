package com.yuyakaido.android.cardstackview.internal;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.yuyakaido.android.cardstackview.R;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import static com.yuyakaido.android.cardstackview.SwipeDirection.Bottom;
import static com.yuyakaido.android.cardstackview.SwipeDirection.Right;

public class CardContainerView extends FrameLayout {

    private CardStackOption option;

    private float viewOriginX = 0f;
    private float viewOriginY = 0f;
    private float motionOriginX = 0f;
    private float motionOriginY = 0f;
    private boolean isDragging = false;
    private boolean isDraggable = true;

    private ViewGroup contentContainer = null;
    private ViewGroup overlayContainer = null;
    private View leftOverlayView = null;
    private View rightOverlayView = null;
    private View bottomOverlayView = null;
    private View topOverlayView = null;

    private ContainerEventListener containerEventListener = null;
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (containerEventListener != null) {
                containerEventListener.onContainerClicked();
            }
            return true;
        }
    };
    private GestureDetector gestureDetector = new GestureDetector(getContext(), gestureListener);

    public interface ContainerEventListener {
        void onContainerDragging(float percentX, float percentY);
        void onContainerSwiped(Point point, SwipeDirection direction);
        void onContainerMovedToOrigin();
        void onContainerClicked();
    }

    public CardContainerView(Context context) {
        this(context, null);
    }

    public CardContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), R.layout.card_frame, this);
        contentContainer = (ViewGroup) findViewById(R.id.card_frame_content_container);
        overlayContainer = (ViewGroup) findViewById(R.id.card_frame_overlay_container);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        if (!option.isSwipeEnabled || !isDraggable) {
            return true;
        }

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(event);
                getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                handleActionUp(event);
                getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!handleActionMove(event)) {
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }

        return true;
    }

    private void handleActionDown(MotionEvent event) {
        motionOriginX = event.getRawX();
        motionOriginY = event.getRawY();
    }

    private void handleActionUp(MotionEvent event) {
        if (isDragging) {
            isDragging = false;

            float motionCurrentX = event.getRawX();
            float motionCurrentY = event.getRawY();

            Point point = Util.getTargetPoint(motionOriginX, motionOriginY, motionCurrentX, motionCurrentY);
            SwipeDirection direction = getDirection(motionCurrentX, motionCurrentY);

            float percent = 0f;
            if (direction == SwipeDirection.Left || direction == SwipeDirection.Right) {
                percent = getPercentX();
            } else {
                percent = getPercentY();
            }

            if (Math.abs(percent) > option.swipeThreshold) {
                if (option.swipeDirection.contains(direction)) {
                    if (containerEventListener != null) {
                        containerEventListener.onContainerSwiped(point, direction);
                    }
                } else {
                    moveToOrigin();
                    if (containerEventListener != null) {
                        containerEventListener.onContainerMovedToOrigin();
                    }
                }
            } else {
                moveToOrigin();
                if (containerEventListener != null) {
                    containerEventListener.onContainerMovedToOrigin();
                }
            }
        }

        motionOriginX = event.getRawX();
        motionOriginY = event.getRawY();
    }

    private boolean handleActionMove(MotionEvent event) {
        if (option.swipeDirection.contains(getDirection(event.getRawX(), event.getRawY()))) {
            isDragging = true;

            updateTranslation(event);
            updateRotation();
            updateAlpha();

            if (containerEventListener != null) {
                containerEventListener.onContainerDragging(getPercentX(), getPercentY());
            }

            return true;
        }

        return false;
    }

    public boolean isSwipingBack(MotionEvent actionDownEvent, MotionEvent event) {
        return MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_MOVE && getDirection(actionDownEvent.getRawX(), actionDownEvent.getRawY(), event.getRawX(), event.getRawY()) == Right;
    }

    private void updateTranslation(MotionEvent event) {
        float translationX = viewOriginX + event.getRawX() - motionOriginX;
        if (getDirection(event.getRawX(), event.getRawY()) == Right) {
            translationX -= getWidth();
        }

        ViewCompat.setTranslationX(this, translationX);
        ViewCompat.setTranslationY(this, viewOriginY);
    }

    private void updateRotation() {
        ViewCompat.setRotation(this, getPercentX() * 20);
    }

    private void updateAlpha() {
        float percentX = getPercentX();
        float percentY = getPercentY();

        if (Math.abs(percentX) > Math.abs(percentY)){
            if (percentX < 0) {
                showLeftOverlay();
            } else {
                showRightOverlay();
            }
            setOverlayAlpha(Math.abs(percentX));
        }else{
            if (percentY < 0) {
                showTopOverlay();
            } else {
                showBottomOverlay();
            }
            setOverlayAlpha(Math.abs(percentY));
        }
    }

    public void moveToOrigin() {
        animate().translationX(viewOriginX)
                .translationY(viewOriginY)
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(1.0f))
                .setListener(null)
                .start();
    }

    private SwipeDirection getDirection(float motionCurrentX, float motionCurrentY) {
        return getDirection(motionOriginX, motionOriginY, motionCurrentX, motionCurrentY);
    }

    private SwipeDirection getDirection(float motionOriginX, float motionOriginY, float motionCurrentX, float motionCurrentY) {
        Quadrant quadrant = Util.getQuadrant(motionOriginX, motionOriginY, motionCurrentX, motionCurrentY);
        double radian = Util.getRadian(motionOriginX, motionOriginY, motionCurrentX, motionCurrentY);
        double degree;
        switch (quadrant) {
            case TopLeft:
                degree = Math.toDegrees(radian);
                degree = 180 - degree;
                radian = Math.toRadians(degree);
                if (Math.cos(radian) < -0.5) {
                    return SwipeDirection.Left;
                } else {
                    return SwipeDirection.Top;
                }
            case TopRight:
                degree = Math.toDegrees(radian);
                radian = Math.toRadians(degree);
                if (Math.cos(radian) < 0.5) {
                    return SwipeDirection.Top;
                } else {
                    return SwipeDirection.Right;
                }
            case BottomLeft:
                degree = Math.toDegrees(radian);
                degree = 180 + degree;
                radian = Math.toRadians(degree);
                if (Math.cos(radian) < -0.5) {
                    return SwipeDirection.Left;
                } else {
                    return Bottom;
                }
            case BottomRight:
                degree = Math.toDegrees(radian);
                degree = 360 - degree;
                radian = Math.toRadians(degree);
                if (Math.cos(radian) < 0.5) {
                    return Bottom;
                }else{
                    return SwipeDirection.Right;
                }
        }

        return null; // Never should reach here
    }

    public void setContainerEventListener(ContainerEventListener listener) {
        this.containerEventListener = listener;
        viewOriginX = ViewCompat.getTranslationX(this);
        viewOriginY = ViewCompat.getTranslationY(this);
    }

    public void setCardStackOption(CardStackOption option) {
        this.option = option;
    }

    public void setDraggable(boolean isDraggable) {
        this.isDraggable = isDraggable;
    }

    public void reset() {
        ViewCompat.setAlpha(contentContainer, 1f);
        ViewCompat.setAlpha(overlayContainer, 0f);
    }

    public ViewGroup getContentContainer() {
        return contentContainer;
    }

    public ViewGroup getOverlayContainer() {
        return overlayContainer;
    }

    public void setOverlay(int left, int right, int bottom, int top) {
        if (leftOverlayView != null) {
            overlayContainer.removeView(leftOverlayView);
        }
        if (left != 0) {
            leftOverlayView = LayoutInflater.from(getContext()).inflate(left, overlayContainer, false);
            overlayContainer.addView(leftOverlayView);
            ViewCompat.setAlpha(leftOverlayView, 0f);
        }

        if (rightOverlayView != null) {
            overlayContainer.removeView(rightOverlayView);
        }
        if (right != 0) {
            rightOverlayView = LayoutInflater.from(getContext()).inflate(right, overlayContainer, false);
            overlayContainer.addView(rightOverlayView);
            ViewCompat.setAlpha(rightOverlayView, 0f);
        }

        if (bottomOverlayView != null) {
            overlayContainer.removeView(bottomOverlayView);
        }
        if (bottom != 0) {
            bottomOverlayView = LayoutInflater.from(getContext()).inflate(bottom, overlayContainer, false);
            overlayContainer.addView(bottomOverlayView);
            ViewCompat.setAlpha(bottomOverlayView, 0f);
        }

        if (topOverlayView != null) {
            overlayContainer.removeView(topOverlayView);
        }
        if (top != 0) {
            topOverlayView = LayoutInflater.from(getContext()).inflate(top, overlayContainer, false);
            overlayContainer.addView(topOverlayView);
            ViewCompat.setAlpha(topOverlayView, 0f);
        }
    }

    public void setOverlayAlpha(float alpha) {
        ViewCompat.setAlpha(overlayContainer, alpha);
    }

    public void showLeftOverlay() {
        if (leftOverlayView != null) {
            ViewCompat.setAlpha(leftOverlayView, 1f);
        }
        if (rightOverlayView != null) {
            ViewCompat.setAlpha(rightOverlayView, 0f);
        }
        if (bottomOverlayView != null) {
            ViewCompat.setAlpha(bottomOverlayView, 0f);
        }
        if (topOverlayView != null) {
            ViewCompat.setAlpha(topOverlayView, 0f);
        }
    }

    public void showRightOverlay() {
        if (leftOverlayView != null) {
            ViewCompat.setAlpha(leftOverlayView, 0f);
        }

        if (bottomOverlayView != null) {
            ViewCompat.setAlpha(bottomOverlayView, 0f);
        }

        if (topOverlayView != null) {
            ViewCompat.setAlpha(topOverlayView, 0f);
        }

        if (rightOverlayView != null) {
            ViewCompat.setAlpha(rightOverlayView, 1f);
        }
    }

    public void showBottomOverlay() {
        if (leftOverlayView != null) {
            ViewCompat.setAlpha(leftOverlayView, 0f);
        }

        if (bottomOverlayView != null) {
            ViewCompat.setAlpha(bottomOverlayView, 1f);
        }

        if (topOverlayView != null) {
            ViewCompat.setAlpha(topOverlayView, 0f);
        }

        if (rightOverlayView != null) {
            ViewCompat.setAlpha(rightOverlayView, 0f);
        }
    }


    public void showTopOverlay() {
        if (leftOverlayView != null) {
            ViewCompat.setAlpha(leftOverlayView, 0f);
        }

        if (bottomOverlayView != null) {
            ViewCompat.setAlpha(bottomOverlayView, 0f);
        }

        if (topOverlayView != null) {
            ViewCompat.setAlpha(topOverlayView, 1f);
        }

        if (rightOverlayView != null) {
            ViewCompat.setAlpha(rightOverlayView, 0f);
        }
    }

    public float getViewOriginX() {
        return viewOriginX;
    }

    public float getViewOriginY() {
        return viewOriginY;
    }

    public float getPercentX() {
        float percent = 2f * (ViewCompat.getTranslationX(this) - viewOriginX) / getWidth();
        if (percent > 1) {
            percent = 1;
        }
        if (percent < -1) {
            percent = -1;
        }
        return percent;
    }

    public float getPercentY() {
        float percent = 2f * (ViewCompat.getTranslationY(this) - viewOriginY) / getHeight();
        if (percent > 1) {
            percent = 1;
        }
        if (percent < -1) {
            percent = -1;
        }
        return percent;
    }

}
