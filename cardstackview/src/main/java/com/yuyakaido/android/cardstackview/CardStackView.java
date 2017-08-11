package com.yuyakaido.android.cardstackview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.yuyakaido.android.cardstackview.internal.CardContainerView;
import com.yuyakaido.android.cardstackview.internal.CardStackOption;
import com.yuyakaido.android.cardstackview.internal.Util;

import java.util.LinkedList;
import java.util.List;

public class CardStackView extends FrameLayout {

    public interface CardEventListener {
        void onCardDragging(float percentX, float percentY);
        void onCardSwiped(Quadrant quadrant);
        void onCardReversed();
        void onCardMovedToOrigin();
        void onCardClicked(int index);
    }

    private CardStackOption option = new CardStackOption();

    private int topIndex = 0;
    private ArrayAdapter<?> adapter = null;
    private LinkedList<CardContainerView> containers = new LinkedList<>();
    private Point lastPoint = null;
    private CardEventListener cardEventListener = null;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            initialize(false);
        }
    };
    private CardContainerView.ContainerEventListener containerEventListener = new CardContainerView.ContainerEventListener() {
        @Override
        public void onContainerDragging(float percentX, float percentY) {
            update(percentX, percentY);
        }
        @Override
        public void onContainerSwiped(Point point) {
            swipe(point);
        }
        @Override
        public void onContainerMovedToOrigin() {
            initializeCardStackPosition();
            if (cardEventListener != null) {
                cardEventListener.onCardMovedToOrigin();
            }
        }
        @Override
        public void onContainerClicked() {
            if (cardEventListener != null) {
                cardEventListener.onCardClicked(topIndex);
            }
        }
    };

    public CardStackView(Context context) {
        this(context, null);
    }

    public CardStackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardStackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CardStackView);
        setVisibleCount(array.getInt(R.styleable.CardStackView_visibleCardCount, option.visibleCount));
        setSwipeThreshold(array.getInt(R.styleable.CardStackView_swipeThreshold, (int) option.swipeThreshold));
        setStackFrom(StackFrom.values()[array.getInt(R.styleable.CardStackView_stackFrom, option.stackFrom.ordinal())]);
        setElevationEnabled(array.getBoolean(R.styleable.CardStackView_isElevationEnabled, option.isElevationEnabled));
        setEnableSwipeDirections(SwipeDirection.from(array.getInt(R.styleable.CardStackView_enableSwipeDirections, 0)));
        array.recycle();
    }

    private void initialize(boolean shouldReset) {
        resetIfNeeded(shouldReset);
        initializeViews();
        initializeCardStackPosition();
        initializeViewContents();
    }

    private void resetIfNeeded(boolean shouldReset) {
        if (shouldReset) {
            topIndex = 0;
            lastPoint = null;
        }
    }

    private void initializeViews() {
        removeAllViews();
        containers.clear();

        for (int i = 0; i < option.visibleCount; i++) {
            CardContainerView view = (CardContainerView) LayoutInflater.from(getContext())
                    .inflate(R.layout.card_container_view, this, false);
            view.setDraggable(false);
            view.setCardStackOption(option);
            view.setOverlay(option.leftOverlay, option.rightOverlay);
            containers.add(0, view);
            addView(view);
        }

        containers.getFirst().setContainerEventListener(containerEventListener);
    }

    private void initializeCardStackPosition() {
        clear();
        update(0f, 0f);
    }

    private void initializeViewContents() {
        for (int i = 0; i < option.visibleCount; i++) {
            CardContainerView container = containers.get(i);
            int adapterIndex = topIndex + i;

            if (adapterIndex < adapter.getCount()) {
                ViewGroup parent = container.getContentContainer();
                View child = adapter.getView(adapterIndex, parent.getChildAt(0), parent);
                if (parent.getChildCount() == 0) {
                    parent.addView(child);
                }
                container.setVisibility(View.VISIBLE);
            } else {
                container.setVisibility(View.GONE);
            }
        }
        if (!adapter.isEmpty()) {
            getTopView().setDraggable(true);
        }
    }

    private void loadNextView() {
        int lastIndex = topIndex + option.visibleCount - 1;
        boolean hasNextCard = lastIndex < adapter.getCount();
        if (hasNextCard) {
            CardContainerView container = getBottomView();
            container.setDraggable(false);
            ViewGroup parent = container.getContentContainer();
            View child = adapter.getView(lastIndex, parent.getChildAt(0), parent);
            if (parent.getChildCount() == 0) {
                parent.addView(child);
            }
        } else {
            CardContainerView container = getBottomView();
            container.setDraggable(false);
            container.setVisibility(View.GONE);
        }

        boolean hasCard = topIndex < adapter.getCount();
        if (hasCard) {
            getTopView().setDraggable(true);
        }
    }

    private void clear() {
        for (int i = 0; i < option.visibleCount; i++) {
            CardContainerView view = containers.get(i);
            view.reset();
            ViewCompat.setTranslationX(view, 0f);
            ViewCompat.setTranslationY(view, 0f);
            ViewCompat.setScaleX(view, 1f);
            ViewCompat.setScaleY(view, 1f);
            ViewCompat.setRotation(view, 0f);
        }
    }

    private void update(float percentX, float percentY) {
        if (cardEventListener != null) {
            cardEventListener.onCardDragging(percentX, percentY);
        }

        if (!option.isElevationEnabled) {
            return;
        }

        for (int i = 1; i < option.visibleCount; i++) {
            CardContainerView view = containers.get(i);

            float currentScale = 1f - (i * 0.02f);
            float nextScale = 1f - ((i - 1) * 0.02f);
            float percent = currentScale + (nextScale - currentScale) * Math.abs(percentX);
            ViewCompat.setScaleX(view, percent);
            ViewCompat.setScaleY(view, percent);

            float currentTranslationY = i * 30;
            if (option.stackFrom == StackFrom.Top) {
                currentTranslationY *= -1;
            }

            float nextTranslationY = (i - 1) * 30;
            if (option.stackFrom == StackFrom.Top) {
                nextTranslationY *= -1;
            }

            float translationY = currentTranslationY - Math.abs(percentX) * (currentTranslationY - nextTranslationY);
            ViewCompat.setTranslationY(view, translationY);
        }
    }

    public void performReverse(Point point, View prevView, final Animator.AnimatorListener listener) {
        reorderForReverse(prevView);
        CardContainerView topView = getTopView();
        ViewCompat.setTranslationX(topView, point.x);
        ViewCompat.setTranslationY(topView, -point.y);
        topView.animate()
                .translationX(topView.getViewOriginX())
                .translationY(topView.getViewOriginY())
                .setListener(listener)
                .setDuration(400L)
                .start();
    }

    public void performSwipe(Point point, final Animator.AnimatorListener listener) {
        getTopView().animate()
                .translationX(point.x)
                .translationY(-point.y)
                .setDuration(400L)
                .setListener(listener)
                .start();
    }

    public void performSwipe(SwipeDirection direction, AnimatorSet set, final Animator.AnimatorListener listener) {
        if (direction == SwipeDirection.Left) {
            getTopView().showLeftOverlay();
            getTopView().setOverlayAlpha(1f);
        } else if (direction == SwipeDirection.Right) {
            getTopView().showRightOverlay();
            getTopView().setOverlayAlpha(1f);
        }
        set.addListener(listener);
        set.start();
    }

    private void moveToBottom(CardContainerView container) {
        CardStackView parent = (CardStackView) container.getParent();
        if (parent != null) {
            parent.removeView(container);
            parent.addView(container, 0);
        }
    }

    private void moveToTop(CardContainerView container, View child) {
        CardStackView parent = (CardStackView) container.getParent();
        if (parent != null) {
            parent.removeView(container);
            parent.addView(container);

            container.getContentContainer().removeAllViews();
            container.getContentContainer().addView(child);
            container.setVisibility(View.VISIBLE);
        }
    }

    private void reorderForDiscard() {
        moveToBottom(getTopView());
        containers.addLast(containers.removeFirst());
    }

    private void reorderForReverse(View prevView) {
        CardContainerView bottomView = getBottomView();
        moveToTop(bottomView, prevView);
        containers.addFirst(containers.removeLast());
    }

    private void executePostSwipeTask(Point point) {
        reorderForDiscard();

        lastPoint = point;

        initializeCardStackPosition();

        topIndex++;

        if (cardEventListener != null) {
            cardEventListener.onCardSwiped(Util.getQuadrant(
                    getTopView().getViewOriginX(), getTopView().getViewOriginY(),
                    point.x, -point.y));
        }

        loadNextView();

        containers.getLast().setContainerEventListener(null);
        containers.getFirst().setContainerEventListener(containerEventListener);
    }

    private void executePostReverseTask() {
        lastPoint = null;

        initializeCardStackPosition();

        topIndex--;

        if (cardEventListener != null) {
            cardEventListener.onCardReversed();
        }

        containers.getLast().setContainerEventListener(null);
        containers.getFirst().setContainerEventListener(containerEventListener);
    }

    public void setCardEventListener(CardEventListener listener) {
        this.cardEventListener = listener;
    }

    public void setAdapter(ArrayAdapter<?> adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(dataSetObserver);
        initialize(true);
    }

    public void setVisibleCount(int visibleCount) {
        option.visibleCount = visibleCount;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setSwipeThreshold(int swipeThreshold) {
        option.swipeThreshold = swipeThreshold;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setStackFrom(StackFrom stackFrom) {
        option.stackFrom = stackFrom;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setElevationEnabled(boolean elevationEnabled) {
        option.isElevationEnabled = elevationEnabled;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        option.isSwipeEnabled = swipeEnabled;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setEnableSwipeDirections(List<SwipeDirection> enableSwipeDirections) {
        option.enableSwipeDirections = enableSwipeDirections;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setLeftOverlay(int leftOverlay) {
        option.leftOverlay = leftOverlay;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void setRightOverlay(int rightOverlay) {
        option.rightOverlay = rightOverlay;
        if (adapter != null) {
            initialize(false);
        }
    }

    public void swipe(final Point point) {
        performSwipe(point, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                executePostSwipeTask(point);
            }
        });
    }

    public void swipe(SwipeDirection direction, AnimatorSet set) {
        performSwipe(direction, set, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                executePostSwipeTask(new Point(0, -2000));
            }
        });
    }

    public void reverse() {
        if (lastPoint != null) {
            ViewGroup parent = containers.getLast();
            View prevView = adapter.getView(topIndex - 1, null, parent);
            performReverse(lastPoint, prevView, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    executePostReverseTask();
                }
            });
        }
    }

    public CardContainerView getTopView() {
        return containers.getFirst();
    }

    public CardContainerView getBottomView() {
        return containers.getLast();
    }

    public int getTopIndex() {
        return topIndex;
    }

}
