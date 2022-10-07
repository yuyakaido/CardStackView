package com.yuyakaido.android.cardstackview.compose

sealed class SwipeDuration {
    abstract val value: Int

    object Fast : SwipeDuration() {
        override val value: Int
            get() = 300

    }

    object Normal : SwipeDuration() {
        override val value: Int
            get() = 500

    }

    object Slow : SwipeDuration() {
        override val value: Int
            get() = 800

    }

    data class Custom(
        override val value: Int,
    ) : SwipeDuration()
}
