package com.yuyakaido.android.cardstackview.internal;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;

public class CardStackDataObserver extends RecyclerView.AdapterDataObserver {

    private final RecyclerView recyclerView;

    public CardStackDataObserver(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
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

    private CardStackLayoutManager getCardStackLayoutManager() {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof CardStackLayoutManager) {
            return (CardStackLayoutManager) manager;
        }
        throw new IllegalStateException("CardStackView must be set CardStackLayoutManager.");
    }

}
