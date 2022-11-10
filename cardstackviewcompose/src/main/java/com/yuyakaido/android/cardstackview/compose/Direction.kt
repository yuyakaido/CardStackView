package com.yuyakaido.android.cardstackview.compose

sealed interface Direction {
    val isDrag: Boolean

    data class Top(override val isDrag: Boolean) : Direction
    data class Bottom(override val isDrag: Boolean) : Direction
    data class Left(override val isDrag: Boolean) : Direction
    data class Right(override val isDrag: Boolean) : Direction
}

