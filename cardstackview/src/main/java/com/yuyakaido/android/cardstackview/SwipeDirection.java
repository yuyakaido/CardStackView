package com.yuyakaido.android.cardstackview;

import java.util.Arrays;
import java.util.List;

public enum SwipeDirection {
    Left, Right, Top, Bottom;

    public static final List<SwipeDirection> FREEDOM = Arrays
            .asList(SwipeDirection.values());
    public static final List<SwipeDirection> HORIZONTAL = Arrays
            .asList(SwipeDirection.Left, SwipeDirection.Right);
    public static final List<SwipeDirection> VERTICAL = Arrays
            .asList(SwipeDirection.Top, SwipeDirection.Bottom);

    public static List<SwipeDirection> from(int value) {
        switch (value) {
            case 0:
                return FREEDOM;
            case 1:
                return HORIZONTAL;
            case 2:
                return VERTICAL;
            default:
                return FREEDOM;
        }
    }
}
