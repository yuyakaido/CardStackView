package com.yuyakaido.android.cardstackview;

public enum StackFrom {
    Bottom("bottom"), Top("top");

    public static final StackFrom DEFAULT = Bottom;

    private String value;

    StackFrom(String value) {
        this.value = value;
    }

    public static StackFrom from(String value) {
        for (StackFrom direction : values()) {
            if (direction.value.equals(value)) {
                return direction;
            }
        }
        return DEFAULT;
    }
}
