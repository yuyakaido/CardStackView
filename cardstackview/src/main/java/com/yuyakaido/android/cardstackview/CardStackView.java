package com.yuyakaido.android.cardstackview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yuyakaido.android.cardstackview.internal.CardStackDataObserver;
import com.yuyakaido.android.cardstackview.internal.CardStackSnapHelper;

public class CardStackView extends RecyclerView {

    private final CardStackDataObserver observer = new CardStackDataObserver(this);

    public CardStackView(Context context) {
        this(context, null);
    }

    public CardStackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardStackView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    @Override
    public void setLayoutManager(LayoutManager manager) {
        if (manager instanceof CardStackLayoutManager) {
            super.setLayoutManager(manager);
        } else {
            throw new IllegalArgumentException("CardStackView must be set CardStackLayoutManager.");
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (getLayoutManager() == null) {
            setLayoutManager(new CardStackLayoutManager(getContext()));
        }
        super.setAdapter(adapter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
            observer.isRegistered = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null) {
            if (observer.isRegistered) {
                adapter.unregisterAdapterDataObserver(observer);
                observer.isRegistered = false;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();
            manager.updateProportion(event.getX(), event.getY());
        }
        return super.onInterceptTouchEvent(event);
    }

    public void swipe() {
        if (getLayoutManager() instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();
            smoothScrollToPosition(manager.getTopPosition() + 1);
        }
    }

    public void rewind() {
        if (getLayoutManager() instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();
            smoothScrollToPosition(manager.getTopPosition() - 1);
        }
    }

    private void initialize() {
        new CardStackSnapHelper().attachToRecyclerView(this);
    }

}
