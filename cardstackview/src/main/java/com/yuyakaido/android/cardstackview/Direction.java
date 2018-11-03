package com.yuyakaido.android.cardstackview;

import java.util.Arrays;
import java.util.List;

public enum Direction {
    Left,
    Right,
    Top,
    Bottom;

    public static final List<Direction> HORIZONTAL = Arrays.asList(Direction.Left, Direction.Right);
    public static final List<Direction> VERTICAL = Arrays.asList(Direction.Top, Direction.Bottom);
    public static final List<Direction> FREEDOM = Arrays.asList(Direction.values());
}
