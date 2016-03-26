package com.yuyakaido.android.cardstackview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
    private int layoutResourceId;
    private ArrayAdapter<?> adapter;
    private OnTouchListener onTouchListener;
    private CardAnimator cardAnimator;
    private List<View> containers = new ArrayList<>();
    private CardStackEventListener cardStackEventListener;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            init(false);
        }
    };

    public interface CardStackEventListener {
        void onBeginSwipe(Direction direction);
        void onEndSwipe(Direction direction);
        void onSwiping(Direction direction);
        void onDiscarded(int index, Direction direction);
        void onTapUp();
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

    public void setLayoutResourceId(int id) {
        layoutResourceId = id;
    }

    public void setCardStackEventListener(CardStackEventListener listener) {
        cardStackEventListener = listener;
    }

    public void addContainerViews() {
        FrameLayout v =  new FrameLayout(getContext());
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
                    }

                    @Override
                    public void onDragging(MotionEvent e1, MotionEvent e2) {
                        cardAnimator.drag(e1, e2);

                        if (cardStackEventListener != null) {
                            float x1 = e1.getRawX();
                            float y1 = e1.getRawY();
                            float x2 = e2.getRawX();
                            float y2 = e2.getRawY();
                            cardStackEventListener.onSwiping(CardUtil.getDirection(x1, y1, x2, y2));
                        }
                    }

                    @Override
                    public void onEndDrag(MotionEvent e1, MotionEvent e2) {
                        float x1 = e1.getRawX();
                        float y1 = e1.getRawY();
                        float x2 = e2.getRawX();
                        float y2 = e2.getRawY();
                        float distance = CardUtil.getDistance(x1, y1, x2, y2);
                        final Direction direction = CardUtil.getDirection(x1, y1, x2, y2);

                        if (cardStackEventListener != null) {
                            cardStackEventListener.onEndSwipe(direction);
                        }

                        if (distance < 300) {
                            cardAnimator.reverse();
                        } else {
                            cardAnimator.discard(direction, new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator arg0) {
                                    cardAnimator.initCards();
                                    topIndex++;

                                    if (cardStackEventListener != null) {
                                        cardStackEventListener.onDiscarded(topIndex, direction);
                                    }

                                    loadNextView();

                                    containers.get(0).setOnTouchListener(null);
                                    containers.get(containers.size() - 1)
                                            .setOnTouchListener(onTouchListener);
                                }

                            });
                        }
                    }

                    @Override
                    public void onTapUp() {
                        if (cardStackEventListener != null) {
                            cardStackEventListener.onTapUp();
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
            ViewGroup parent = (ViewGroup) containers.get(i);
            int adapterIndex = (topIndex + visibleCount - 1) - i;
            if (adapterIndex > adapter.getCount() - 1) {
                parent.setVisibility(View.GONE);
            } else {
                View child = adapter.getView(adapterIndex, getContentView(), this);
                parent.addView(child);
                parent.setVisibility(View.VISIBLE);
            }
        }
    }

    public View getContentView() {
        View contentView = null;
        if (layoutResourceId != 0) {
            contentView = View.inflate(getContext(), layoutResourceId, null);
        }
        return contentView;

    }

    public void loadNextView() {
        ViewGroup parent = (ViewGroup) containers.get(0);

        int lastIndex = (visibleCount - 1) + topIndex;
        if (lastIndex > adapter.getCount() - 1) {
            parent.setVisibility(View.GONE);
            return;
        }

        View child = adapter.getView(lastIndex, getContentView(), parent);
        parent.removeAllViews();
        parent.addView(child);
    }

}
