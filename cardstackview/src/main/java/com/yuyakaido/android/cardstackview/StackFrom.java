package com.yuyakaido.android.cardstackview;

import java.util.Collections;
import java.util.List;

public enum StackFrom {
    Bottom, Top, Left, Right;
    public static final List<StackFrom> DEFAULT = Collections.singletonList(Top);
}
