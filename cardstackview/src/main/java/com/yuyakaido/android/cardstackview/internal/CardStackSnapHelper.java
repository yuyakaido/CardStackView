package com.yuyakaido.android.cardstackview.internal;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

public class CardStackSnapHelper extends SnapHelper {

    private int velocityX = 0;
    private int velocityY = 0;

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(
            @NonNull RecyclerView.LayoutManager layoutManager,
            @NonNull View targetView
    ) {
        if (layoutManager instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) layoutManager;
            if (manager.findViewByPosition(manager.getTopPosition()) != null) {
                int x = (int) targetView.getTranslationX();
                int y = (int) targetView.getTranslationY();
                if (x != 0 || y != 0) {
                    CardStackSetting setting = manager.getCardStackSetting();
                    float horizontal = Math.abs(x) / (float) targetView.getWidth();
                    float vertical = Math.abs(y) / (float) targetView.getHeight();
                    Duration duration = Duration.fromVelocity(velocityY < velocityX ? velocityX : velocityY);
                    if (duration == Duration.Fast || setting.swipeThreshold < horizontal || setting.swipeThreshold < vertical) {
                        CardStackState state = manager.getCardStackState();
                        if (setting.directions.contains(state.getDirection())) {
                            state.targetPosition = state.topPosition + 1;

                            SwipeAnimationSetting swipeAnimationSetting = new SwipeAnimationSetting.Builder()
                                    .setDirection(setting.swipeAnimationSetting.getDirection())
                                    .setDuration(duration.duration)
                                    .setInterpolator(setting.swipeAnimationSetting.getInterpolator())
                                    .build();
                            manager.setSwipeAnimationSetting(swipeAnimationSetting);

                            this.velocityX = 0;
                            this.velocityY = 0;

                            CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualSwipe, manager);
                            scroller.setTargetPosition(manager.getTopPosition());
                            manager.startSmoothScroll(scroller);
                        } else {
                            CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualCancel, manager);
                            scroller.setTargetPosition(manager.getTopPosition());
                            manager.startSmoothScroll(scroller);
                        }
                    } else {
                        CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.ManualCancel, manager);
                        scroller.setTargetPosition(manager.getTopPosition());
                        manager.startSmoothScroll(scroller);
                    }
                }
            }
        }
        return new int[2];
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) layoutManager;
            View view = manager.findViewByPosition(manager.getTopPosition());
            if (view != null) {
                int x = (int) view.getTranslationX();
                int y = (int) view.getTranslationY();
                if (x == 0 && y == 0) {
                    return null;
                }
                return view;
            }
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(
            RecyclerView.LayoutManager layoutManager,
            int velocityX,
            int velocityY
    ) {
        this.velocityX = Math.abs(velocityX);
        this.velocityY = Math.abs(velocityY);
        if (layoutManager instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) layoutManager;
            return manager.getTopPosition();
        }
        return RecyclerView.NO_POSITION;
    }

}
