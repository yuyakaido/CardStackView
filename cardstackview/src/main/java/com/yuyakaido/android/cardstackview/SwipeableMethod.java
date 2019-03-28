package com.yuyakaido.android.cardstackview;

public enum SwipeableMethod {
    ManualAndAutomatic,
    Manual,
    Automatic,
    None;

    boolean canManualSwipe() {
        return this == ManualAndAutomatic || this == Manual;
    }

    boolean canAutomaticSwipe() {
        return this == ManualAndAutomatic || this == Automatic;
    }
}
