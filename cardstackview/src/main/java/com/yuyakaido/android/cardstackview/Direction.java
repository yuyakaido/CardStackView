package com.yuyakaido.android.cardstackview;

public enum Direction {
    TopLeft(0), TopRight(1), BottomLeft(2), BottomRight(3);

    private int index;

    Direction(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
