package com.yuyakaido.android.cardstackview.compose

enum class SwipeMethod {
    AutomaticAndManual,
    Automatic,
    Manual,
    None;

    fun canSwipe(): Boolean {
        return canSwipeAutomatically() || canSwipeManually()
    }

    fun canSwipeAutomatically(): Boolean {
        return this == AutomaticAndManual || this == Automatic
    }

    fun canSwipeManually(): Boolean {
        return this == AutomaticAndManual || this == Manual
    }
}