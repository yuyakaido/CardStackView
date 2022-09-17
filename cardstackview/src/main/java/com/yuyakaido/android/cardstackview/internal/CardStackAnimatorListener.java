package com.yuyakaido.android.cardstackview.internal;

import android.animation.Animator;

public abstract class CardStackAnimatorListener implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation, boolean isReverse) {
        // not need to implement
    }

    @Override
    public void onAnimationEnd(Animator animation, boolean isReverse) {
        // not need to implement
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        // not need to implement
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        // not need to implement
    }
}