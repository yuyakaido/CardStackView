package com.yuyakaido.android.cardstackview.compose

internal data class PaddingBetweenCards(
    val paddingX: Float,
    val paddingY: Float,
) {
    companion object {
        fun get(translationInterval: Float, stackFrom: StackFrom): PaddingBetweenCards {
            return when (stackFrom) {
                StackFrom.None -> {
                    PaddingBetweenCards(0f, 0f)
                }
                StackFrom.Top -> {
                    PaddingBetweenCards(0f, translationInterval)
                }
                StackFrom.TopAndLeft -> {
                    PaddingBetweenCards(translationInterval, translationInterval)
                }
                StackFrom.TopAndRight -> {
                    PaddingBetweenCards(-translationInterval, translationInterval)
                }
                StackFrom.Bottom -> {
                    PaddingBetweenCards(0f, -translationInterval)
                }
                StackFrom.BottomAndLeft -> {
                    PaddingBetweenCards(translationInterval, -translationInterval)
                }
                StackFrom.BottomAndRight -> {
                    PaddingBetweenCards(-translationInterval, -translationInterval)
                }
                StackFrom.Left -> {
                    PaddingBetweenCards(translationInterval, 0f)
                }
                StackFrom.Right -> {
                    PaddingBetweenCards(-translationInterval, 0f)
                }
            }
        }
    }
}
