package com.yuyakaido.android.cardstackview.sample;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class SpotDiffCallback extends DiffUtil.Callback {

    private final List<Spot> oldList;
    private final List<Spot> newList;

    public SpotDiffCallback(List<Spot> oldList, List<Spot> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {
        return oldList.get(oldPosition).id == newList.get(newPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {
        Spot oldSpot = oldList.get(oldPosition);
        Spot newSpot = newList.get(newPosition);
        return oldSpot.name.equals(newSpot.name)
                && oldSpot.city.equals(newSpot.city)
                && oldSpot.url.equals(newSpot.url);
    }

}
