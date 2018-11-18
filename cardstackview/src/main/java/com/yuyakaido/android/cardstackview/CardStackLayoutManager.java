package com.yuyakaido.android.cardstackview;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yuyakaido.android.cardstackview.internal.CardStackSetting;
import com.yuyakaido.android.cardstackview.internal.CardStackSmoothScroller;
import com.yuyakaido.android.cardstackview.internal.CardStackState;
import com.yuyakaido.android.cardstackview.internal.DisplayUtil;

import java.util.List;

public class CardStackLayoutManager
        extends RecyclerView.LayoutManager
        implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private final Context context;

    private CardStackListener listener = CardStackListener.DEFAULT;
    private CardStackSetting setting = new CardStackSetting();
    private CardStackState state = new CardStackState();

    public CardStackLayoutManager(Context context) {
        this(context, CardStackListener.DEFAULT);
    }

    public CardStackLayoutManager(Context context, CardStackListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        update(recycler);
    }

    @Override
    public boolean canScrollHorizontally() {
        return setting.canScrollHorizontal;
    }

    @Override
    public boolean canScrollVertically() {
        return setting.canScrollVertical;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State s) {
        if (state.status != CardStackState.Status.SwipeAnimating) {
            state.dx -= dx;
            update(recycler);
            return dx;
        }
        return 0;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State s) {
        if (state.status != CardStackState.Status.SwipeAnimating) {
            state.dy -= dy;
            update(recycler);
            return dy;
        }
        return 0;
    }

    @Override
    public void onScrollStateChanged(int s) {
        switch (s) {
            case RecyclerView.SCROLL_STATE_IDLE:
                if (state.status != CardStackState.Status.PrepareSwipeAnimation) {
                    if (state.targetPosition == RecyclerView.NO_POSITION) {
                        state.next(CardStackState.Status.Idle);
                    } else {
                        if (state.topPosition < state.targetPosition) {
                            smoothScrollToNext(state.targetPosition);
                        } else if (state.targetPosition < state.topPosition) {
                            smoothScrollToPrevious(state.targetPosition);
                        } else {
                            state.next(CardStackState.Status.Idle);
                            state.targetPosition = RecyclerView.NO_POSITION;
                        }
                    }
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                state.next(CardStackState.Status.Dragging);
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                if (state.status != CardStackState.Status.PrepareSwipeAnimation) {
                    if (state.targetPosition == RecyclerView.NO_POSITION) {
                        state.next(CardStackState.Status.Idle);
                    } else {
                        if (state.topPosition < state.targetPosition) {
                            state.next(CardStackState.Status.PrepareSwipeAnimation);
                        } else if (state.targetPosition < state.topPosition) {
                            state.next(CardStackState.Status.RewindAnimating);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }

    @Override
    public void scrollToPosition(int position) {
        if (position == state.topPosition || position < 0 || getItemCount() < position) {
            state.next(CardStackState.Status.Idle);
            state.targetPosition = RecyclerView.NO_POSITION;
        } else if (state.status == CardStackState.Status.Idle) {
            state.topPosition = position;
            requestLayout();
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State s, int position) {
        if (position == state.topPosition || position < 0 || getItemCount() < position) {
            state.next(CardStackState.Status.Idle);
            state.targetPosition = RecyclerView.NO_POSITION;
        } else if (state.status == CardStackState.Status.Idle) {
            smoothScrollToPosition(position);
        }
    }

    @NonNull
    public CardStackSetting getCardStackSetting() {
        return setting;
    }

    @NonNull
    public CardStackState getCardStackState() {
        return state;
    }

    @NonNull
    public CardStackListener getCardStackListener() {
        return listener;
    }

    void updateProportion(float x, float y) {
        if (getTopPosition() < getItemCount()) {
            View view = findViewByPosition(getTopPosition());
            float half = getHeight() / 2.0f;
            state.proportion = -(y - half - view.getTop()) / half;
        }
    }

    private void update(RecyclerView.Recycler recycler) {
        state.width = getWidth();
        state.height = getHeight();

        if (state.status == CardStackState.Status.PrepareSwipeAnimation && (state.targetPosition == RecyclerView.NO_POSITION || state.topPosition < state.targetPosition)) {
            if (Math.abs(state.dx) > getWidth() || Math.abs(state.dy) > getHeight()) {
                state.next(CardStackState.Status.SwipeAnimating);
                state.topPosition++;
                final Direction direction = state.getDirection();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCardSwiped(direction);
                    }
                });
                state.dx = 0;
                state.dy = 0;
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int position = getPosition(child);
            state.cache.put(position, child);
        }

        for (int i = 0; i < state.cache.size(); i++) {
            detachView(state.cache.valueAt(i));
        }

        final int parentTop = getPaddingTop();
        final int parentLeft = getPaddingLeft();
        final int parentRight = getWidth() - getPaddingLeft();
        final int parentBottom = getHeight() - getPaddingBottom();
        for (int i = state.topPosition; i < state.topPosition + setting.visibleCount && i < getItemCount(); i++) {
            View child = state.cache.get(i);
            if (child == null) {
                child = recycler.getViewForPosition(i);
                addView(child, 0);
                measureChildWithMargins(child, 0, 0);
                layoutDecoratedWithMargins(child, parentLeft, parentTop, parentRight, parentBottom);
            } else {
                attachView(child, 0);
                state.cache.remove(i);
            }

            resetTranslation(child);
            resetScale(child);
            resetRotation(child);
            resetOverlay(child);

            if (i == state.topPosition) {
                updateTranslation(child);
                resetScale(child);
                updateRotation(child);
                updateOverlay(child);
            } else {
                int currentIndex = i - state.topPosition;
                updateTranslation(child, currentIndex);
                updateScale(child, currentIndex);
                resetRotation(child);
                resetOverlay(child);
            }
        }

        for (int i = 0; i < state.cache.size(); i++) {
            removeAndRecycleView(state.cache.valueAt(i), recycler);
        }
        state.cache.clear();

        if (state.status == CardStackState.Status.Dragging) {
            listener.onCardDragging(state.getDirection(), state.getRatio());
        }
    }

    private void updateTranslation(View view) {
        view.setTranslationX(state.dx);
        view.setTranslationY(state.dy);
    }

    private void updateTranslation(View view, int index) {
        int nextIndex = index - 1;
        int translationPx = DisplayUtil.dpToPx(context, setting.translationInterval);
        float currentTranslation = index * translationPx;
        float nextTranslation = nextIndex * translationPx;
        float targetTranslation = currentTranslation - (currentTranslation - nextTranslation) * state.getRatio();
        switch (setting.stackFrom) {
            case None:
                // Do nothing
                break;
            case Top:
                view.setTranslationY(-targetTranslation);
                break;
            case Bottom:
                view.setTranslationY(targetTranslation);
                break;
            case Left:
                view.setTranslationX(-targetTranslation);
                break;
            case Right:
                view.setTranslationX(targetTranslation);
                break;
        }
    }

    private void resetTranslation(View view) {
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
    }

    private void updateScale(View view, int index) {
        int nextIndex = index - 1;
        float currentScale = 1.0f - index * (1.0f - setting.scaleInterval);
        float nextScale = 1.0f - nextIndex * (1.0f - setting.scaleInterval);
        float targetScale = currentScale + (nextScale - currentScale) * state.getRatio();
        switch (setting.stackFrom) {
            case None:
                view.setScaleX(targetScale);
                view.setScaleY(targetScale);
                break;
            case Top:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case Bottom:
                view.setScaleX(targetScale);
                // TODO Should handle ScaleY
                break;
            case Left:
                // TODO Should handle ScaleX
                view.setScaleY(targetScale);
                break;
            case Right:
                // TODO Should handle ScaleX
                view.setScaleY(targetScale);
                break;
        }
    }

    private void resetScale(View view) {
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
    }

    private void updateRotation(View view) {
        float degree = state.dx * setting.maxDegree / getWidth() * state.proportion;
        view.setRotation(degree);
    }

    private void resetRotation(View view) {
        view.setRotation(0.0f);
    }

    private void updateOverlay(View view) {
        View leftOverlay = view.findViewById(R.id.left_overlay);
        if (leftOverlay != null) {
            leftOverlay.setAlpha(0.0f);
        }
        View rightOverlay = view.findViewById(R.id.right_overlay);
        if (rightOverlay != null) {
            rightOverlay.setAlpha(0.0f);
        }
        View topOverlay = view.findViewById(R.id.top_overlay);
        if (topOverlay != null) {
            topOverlay.setAlpha(0.0f);
        }
        View bottomOverlay = view.findViewById(R.id.bottom_overlay);
        if (bottomOverlay != null) {
            bottomOverlay.setAlpha(0.0f);
        }
        Direction direction = state.getDirection();
        switch (direction) {
            case Left:
                if (leftOverlay != null) {
                    leftOverlay.setAlpha(state.getRatio());
                }
                break;
            case Right:
                if (rightOverlay != null) {
                    rightOverlay.setAlpha(state.getRatio());
                }
                break;
            case Top:
                if (topOverlay != null) {
                    topOverlay.setAlpha(state.getRatio());
                }
                break;
            case Bottom:
                if (bottomOverlay != null) {
                    bottomOverlay.setAlpha(state.getRatio());
                }
                break;
        }
    }

    private void resetOverlay(View view) {
        View leftOverlay = view.findViewById(R.id.left_overlay);
        if (leftOverlay != null) {
            leftOverlay.setAlpha(0.0f);
        }
        View rightOverlay = view.findViewById(R.id.right_overlay);
        if (rightOverlay != null) {
            rightOverlay.setAlpha(0.0f);
        }
        View topOverlay = view.findViewById(R.id.top_overlay);
        if (topOverlay != null) {
            topOverlay.setAlpha(0.0f);
        }
        View bottomOverlay = view.findViewById(R.id.bottom_overlay);
        if (bottomOverlay != null) {
            bottomOverlay.setAlpha(0.0f);
        }
    }

    private void smoothScrollToPosition(int position) {
        if (state.topPosition < position) {
            smoothScrollToNext(position);
        } else {
            smoothScrollToPrevious(position);
        }
    }

    private void smoothScrollToNext(int position) {
        state.proportion = 0.0f;
        state.targetPosition = position;
        CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.AutomaticSwipe, this);
        scroller.setTargetPosition(state.topPosition);
        startSmoothScroll(scroller);
    }

    private void smoothScrollToPrevious(int position) {
        state.proportion = 0.0f;
        state.targetPosition = position;
        state.topPosition--;
        CardStackSmoothScroller scroller = new CardStackSmoothScroller(CardStackSmoothScroller.ScrollType.AutomaticRewind, this);
        scroller.setTargetPosition(state.topPosition);
        startSmoothScroll(scroller);
    }

    public int getTopPosition() {
        return state.topPosition;
    }

    public void setTopPosition(int topPosition) {
        state.topPosition = topPosition;
    }

    public void setStackFrom(@NonNull StackFrom stackFrom) {
        setting.stackFrom = stackFrom;
    }

    public void setVisibleCount(@IntRange(from = 1) int visibleCount) {
        if (visibleCount < 1) {
            throw new IllegalArgumentException("VisibleCount must be greater than 0.");
        }
        setting.visibleCount = visibleCount;
    }

    public void setTranslationInterval(@FloatRange(from = 0.0f) float translationInterval) {
        if (translationInterval < 0.0f) {
            throw new IllegalArgumentException("TranslationInterval must be greater than or equal 0.0f");
        }
        setting.translationInterval = translationInterval;
    }

    public void setScaleInterval(@FloatRange(from = 0.0f) float scaleInterval) {
        if (scaleInterval < 0.0f) {
            throw new IllegalArgumentException("ScaleInterval must be greater than or equal 0.0f.");
        }
        setting.scaleInterval = scaleInterval;
    }

    public void setSwipeThreshold(@FloatRange(from = 0.0f, to = 1.0f) float swipeThreshold) {
        if (swipeThreshold < 0.0f || 1.0f < swipeThreshold) {
            throw new IllegalArgumentException("SwipeThreshold must be 0.0f to 1.0f.");
        }
        setting.swipeThreshold = swipeThreshold;
    }

    public void setMaxDegree(@FloatRange(from = -360.0f, to = 360.0f) float maxDegree) {
        if (maxDegree < -360.0f || 360.0f < maxDegree) {
            throw new IllegalArgumentException("MaxDegree must be -360.0f to 360.0f");
        }
        setting.maxDegree = maxDegree;
    }

    public void setDirections(@NonNull List<Direction> directions) {
        setting.directions = directions;
    }

    public void setCanScrollHorizontal(boolean canScrollHorizontal) {
        setting.canScrollHorizontal = canScrollHorizontal;
    }

    public void setCanScrollVertical(boolean canScrollVertical) {
        setting.canScrollVertical = canScrollVertical;
    }

    public void setSwipeAnimationSetting(@NonNull SwipeAnimationSetting swipeAnimationSetting) {
        setting.swipeAnimationSetting = swipeAnimationSetting;
    }

    public void setRewindAnimationSetting(@NonNull RewindAnimationSetting rewindAnimationSetting) {
        setting.rewindAnimationSetting = rewindAnimationSetting;
    }

}
