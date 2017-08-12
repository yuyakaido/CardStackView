package com.yuyakaido.android.cardstackview.internal;

import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.util.List;

public class CardStackOption {
    public int visibleCount = 3;
    public float swipeThreshold = 0.75f;
    public StackFrom stackFrom = StackFrom.DEFAULT;
    public boolean isElevationEnabled = true;
    public boolean isSwipeEnabled = true;
    public int leftOverlay = 0;
    public int rightOverlay = 0;
    public List<SwipeDirection> enableSwipeDirections = SwipeDirection.FREEDOM;
}
