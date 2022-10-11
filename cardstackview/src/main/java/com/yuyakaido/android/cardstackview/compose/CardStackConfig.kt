package com.yuyakaido.android.cardstackview.compose

data class CardStackConfig(
    val swipeDuration: Int = 500,
    val swipeThreshold: Float = 0.3f, // 0.0f ~ 1.0f
    val scaleInterval: Float = 0.95f, // 0.0f - 1.0f
    val translationInterval: Float = 20F,
    val stackFrom: StackFrom = StackFrom.None,
    val visibleCount: Int = 3,
    val maxDegree: Float = 20f,
    val rotateRatio: Int = 60,
    val swipeMethod: SwipeMethod = SwipeMethod.AutomaticAndManual,
)
