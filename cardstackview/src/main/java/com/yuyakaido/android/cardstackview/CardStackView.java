package com.yuyakaido.android.cardstackview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class CardStackView extends RelativeLayout {
    private int topIndex = 0;
    private int visibleCount = 4;
    private ArrayAdapter<?> adapter;
    private OnTouchListener onTouchListener;
    private CardAnimator cardAnimator;
    private List<ViewGroup> containers = new ArrayList<>();
    private CardStackEventListener cardStackEventListener;
    private Direction lastDirection;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            init(false);
        }
    };

    public interface CardStackEventListener {
        void onBeginSwipe(int index, Direction direction);
        void onEndSwipe(Direction direction);
        void onSwiping(Direction direction);
        void onDiscarded(int index, Direction direction);
        void onTapUp(int index);
    }

    public CardStackView(Context context) {
        this(context, null);
    }

    public CardStackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardStackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(boolean resetIndex) {
        if (resetIndex) {
            topIndex = 0;
            lastDirection = null;
        }

        removeAllViews();
        containers.clear();

        for (int i = 0; i < visibleCount; i++) {
            addContainerViews();
        }
        setupAnimation();
        loadViews();
    }

    public void setAdapter(ArrayAdapter<?> adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(dataSetObserver);
        init(true);
    }

    public void setCardStackEventListener(CardStackEventListener listener) {
        cardStackEventListener = listener;
    }

    public void addContainerViews() {
        FrameLayout v = new FrameLayout(getContext());
        containers.add(v);
        addView(v);
    }

    public void setupAnimation() {
        cardAnimator = new CardAnimator(getContext(), containers);
        cardAnimator.initCards();

        final DragGestureDetector dragGestureDetector = new DragGestureDetector(getContext(),
                new DragGestureDetector.DragListener() {
                    @Override
                    public void onBeginDrag(MotionEvent e1, MotionEvent e2) {
                        cardAnimator.drag(e1, e2);
                        if (cardStackEventListener != null) {
                            float oldX = e1.getRawX();
                            float oldY = e1.getRawY();
                            float newX = e2.getRawX();
                            float newY = e2.getRawY();
                            final Direction direction = CardUtil.getDirection(oldX, oldY, newX, newY);
                            cardStackEventListener.onBeginSwipe(topIndex, direction);
                        }
                    }

                    @Override
                    public void onDragging(MotionEvent e1, MotionEvent e2) {
                        cardAnimator.drag(e1, e2);

                        if (cardStackEventListener != null) {
                            float oldX = e1.getRawX();
                            float oldY = e1.getRawY();
                            float newX = e2.getRawX();
                            float newY = e2.getRawY();
                            final Direction direction = CardUtil.getDirection(oldX, oldY, newX, newY);
                            cardStackEventListener.onSwiping(direction);
                        }
                    }

                    @Override
                    public void onEndDrag(MotionEvent e1, MotionEvent e2) {
                        float oldX = e1.getRawX();
                        float oldY = e1.getRawY();
                        float newX = e2.getRawX();
                        float newY = e2.getRawY();
                        float distance = CardUtil.getDistance(oldX, oldY, newX, newY);
                        final Direction direction = CardUtil.getDirection(oldX, oldY, newX, newY);

                        if (cardStackEventListener != null) {
                            cardStackEventListener.onEndSwipe(direction);
                        }

                        if (distance < 300) {
                            cardAnimator.moveToOrigin();
                        } else {
                            discard(direction);
                        }
                    }

                    @Override
                    public void onTapUp() {
                        if (cardStackEventListener != null) {
                            cardStackEventListener.onTapUp(topIndex);
                        }
                    }
                });

        onTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                dragGestureDetector.onTouchEvent(event);
                return true;
            }
        };
        containers.get(containers.size() - 1).setOnTouchListener(onTouchListener);
    }

    public void loadViews() {
        for (int i = visibleCount - 1; i >= 0; i--) {
            ViewGroup parent = containers.get(i);
            int adapterIndex = (topIndex + visibleCount - 1) - i;
            if (adapterIndex > adapter.getCount() - 1) {
                parent.setVisibility(View.GONE);
            } else {
                View child = adapter.getView(adapterIndex, parent.getChildAt(0), this);
                parent.addView(child);
                parent.setVisibility(View.VISIBLE);
            }
        }
    }

    public void loadNextView() {
        ViewGroup parent = containers.get(0);

        int lastIndex = (visibleCount - 1) + topIndex;
        if (lastIndex > adapter.getCount() - 1) {
            parent.setVisibility(View.GONE);
            return;
        }

        View child = adapter.getView(lastIndex, parent.getChildAt(0), parent);
        parent.removeAllViews();
        parent.addView(child);
    }

    public void discard(final Direction direction) {
        cardAnimator.discard(direction, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator arg0) {
                lastDirection = direction;

                cardAnimator.initCards();

                if (cardStackEventListener != null) {
                    cardStackEventListener.onDiscarded(topIndex, direction);
                }

                topIndex++;

                loadNextView();

                containers.get(0).setOnTouchListener(null);
                containers.get(containers.size() - 1)
                        .setOnTouchListener(onTouchListener);
            }

        });
    }

    public void discard(ObjectAnimator topAnimator) {
        cardAnimator.discard(Direction.BottomRight, topAnimator, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator arg0) {
                lastDirection = Direction.BottomRight;

                cardAnimator.initCards();

                if (cardStackEventListener != null) {
                    cardStackEventListener.onDiscarded(topIndex, Direction.BottomRight);
                }

                topIndex++;

                loadNextView();

                containers.get(0).setOnTouchListener(null);
                containers.get(containers.size() - 1)
                        .setOnTouchListener(onTouchListener);
            }

        });
    }

    public void reverse() {
        if (lastDirection != null) {
            topIndex--;

            ViewGroup parent = containers.get(0);
            View prevView = adapter.getView(topIndex, null, parent);
            cardAnimator.reverse(lastDirection, prevView, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lastDirection = null;

                    containers.get(0).setOnTouchListener(null);
                    containers.get(containers.size() - 1)
                            .setOnTouchListener(onTouchListener);
                }
            });
        }
    }

    public int getTopIndex() {
        return topIndex;
    }

    public ViewGroup getTopView() {
        return cardAnimator.getTopView();
    }

}
