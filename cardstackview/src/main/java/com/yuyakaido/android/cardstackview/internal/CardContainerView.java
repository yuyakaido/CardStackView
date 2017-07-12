package com.yuyakaido.android.cardstackview.internal;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.yuyakaido.android.cardstackview.R;

public class CardContainerView extends FrameLayout {

    private CardStackOption option;

    private float viewOriginX = 0f;
    private float viewOriginY = 0f;
    private float motionOriginX = 0f;
    private float motionOriginY = 0f;
    private boolean isDragging = false;

    private ViewGroup contentContainer = null;
    private ViewGroup overlayContainer = null;
    private View leftOverlayView = null;
    private View rightOverlayView = null;

    private ContainerEventListener listener = null;

    public interface ContainerEventListener {
        void onContainerDragging(float percentX, float percentY);
        void onContainerSwiped(Point point);
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
        inflate(getContext(), R.layout.view_card, this);
        contentContainer = (ViewGroup) findViewById(R.id.view_card_container_content);
        overlayContainer = (ViewGroup) findViewById(R.id.view_card_container_overlay);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!option.isSwipeEnabled) {
            return true;
        }

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
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

            float distance = Util.getDistance(
                    motionOriginX, motionOriginY, motionCurrentX, motionCurrentY);
            if (distance > option.swipeThreshold) {
                if (listener != null) {
                    listener.onContainerSwiped(Util.getTargetPoint(
                            motionOriginX, motionOriginY, motionCurrentX, motionCurrentY));
                }
            } else {
                moveToOrigin();
                if (listener != null) {
                    listener.onContainerMovedToOrigin();
                }
            }
        } else {
            if (listener != null) {
                listener.onContainerClicked();
            }
        }

        motionOriginX = event.getRawX();
        motionOriginY = event.getRawY();
    }

    private void handleActionMove(MotionEvent event) {
        isDragging = true;

        updateTranslation(event);
        updateRotation();
        updateAlpha();

        if (listener != null) {
            listener.onContainerDragging(getPercentX(), getPercentY());
        }
    }

    private void updateTranslation(MotionEvent event) {
        ViewCompat.setTranslationX(this, viewOriginX + event.getRawX() - motionOriginX);
        ViewCompat.setTranslationY(this, viewOriginY + event.getRawY() - motionOriginY);
    }

    private void updateRotation() {
        ViewCompat.setRotation(this, getPercentX() * 20);
    }

    private void updateAlpha() {
        if (getPercentX() < 0) {
            showLeftOverlay();
        } else {
            showRightOverlay();
        }
        setOverlayAlpha(Math.abs(getPercentX()));
    }

    private void moveToOrigin() {
        animate().translationX(viewOriginX)
                .translationY(viewOriginY)
                .setDuration(250L)
                .setInterpolator(new OvershootInterpolator())
                .setListener(null)
                .start();
    }

    public void setContainerEventListener(ContainerEventListener listener) {
        this.listener = listener;
        viewOriginX = ViewCompat.getTranslationX(this);
        viewOriginY = ViewCompat.getTranslationY(this);
    }

    public void setCardStackOption(CardStackOption option) {
        this.option = option;
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

    public void setOverlay(int left, int right) {
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
    }

    public void showRightOverlay() {
        if (leftOverlayView != null) {
            ViewCompat.setAlpha(leftOverlayView, 0f);
        }
        if (rightOverlayView != null) {
            ViewCompat.setAlpha(rightOverlayView, 1f);
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
