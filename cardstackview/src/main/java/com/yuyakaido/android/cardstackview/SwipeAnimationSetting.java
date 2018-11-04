package com.yuyakaido.android.cardstackview;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.yuyakaido.android.cardstackview.internal.AnimationSetting;

public class SwipeAnimationSetting implements AnimationSetting {

    private final Direction direction;
    private final int duration;
    private final Interpolator interpolator;

    private SwipeAnimationSetting(
            Direction direction,
            int duration,
            Interpolator interpolator
    ) {
        this.direction = direction;
        this.duration = duration;
        this.interpolator = interpolator;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public Interpolator getInterpolator() {
        return interpolator;
    }

    public static class Builder {
        private Direction direction = Direction.Right;
        private int duration = 200;
        private Interpolator interpolator = new AccelerateInterpolator();

        public Builder setDirection(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder setDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public SwipeAnimationSetting build() {
            return new SwipeAnimationSetting(
                    direction,
                    duration,
                    interpolator
            );
        }
    }

}
