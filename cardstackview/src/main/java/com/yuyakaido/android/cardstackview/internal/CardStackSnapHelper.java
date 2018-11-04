package com.yuyakaido.android.cardstackview.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;

public class CardStackSnapHelper extends SnapHelper {

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
                    if (setting.swipeThreshold < horizontal || setting.swipeThreshold < vertical) {
                        CardStackState state = manager.getCardStackState();
                        if (setting.directions.contains(state.getDirection())) {
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
                int width = manager.getWidth();
                int height = manager.getHeight();
                if (x > width || y > height || (x == 0 && y == 0)) {
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
        if (layoutManager instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) layoutManager;
            return manager.getTopPosition();
        }
        return RecyclerView.NO_POSITION;
    }

}
