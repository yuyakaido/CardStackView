package com.yuyakaido.android.cardstackview.internal;

import android.view.animation.Interpolator;

import com.yuyakaido.android.cardstackview.Direction;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
