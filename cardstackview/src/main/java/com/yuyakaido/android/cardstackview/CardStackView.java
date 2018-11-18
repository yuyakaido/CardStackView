package com.yuyakaido.android.cardstackview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yuyakaido.android.cardstackview.internal.CardStackSnapHelper;

public class CardStackView extends RecyclerView implements View.OnTouchListener {

    private final AdapterDataObserver observer = new AdapterDataObserver() {
        private CardStackLayoutManager getCardStackLayoutManager() {
            LayoutManager manager = getLayoutManager();
            if (manager instanceof CardStackLayoutManager) {
                return (CardStackLayoutManager) manager;
            }
            throw new IllegalStateException("CardStackView must be set CardStackLayoutManager.");
        }
        @Override
        public void onChanged() {
            CardStackLayoutManager manager = getCardStackLayoutManager();
            manager.setTopPosition(0);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            // Do nothing
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            // Do nothing
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            // Do nothing
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            CardStackLayoutManager manager = getCardStackLayoutManager();
            if (positionStart == 0) {
                manager.setTopPosition(0);
            }
            manager.removeAllViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            // Do nothing
        }
    };

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
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(observer);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();
            manager.updateProportion(event.getX(), event.getY());
        }
        return false;
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
        setOnTouchListener(this);
    }

}
