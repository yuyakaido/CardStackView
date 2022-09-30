package com.yuyakaido.android.cardstackview.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> rememberCardStackViewController(): CardStackViewController<T> {
    return remember { CardStackViewController() }
}

class CardStackViewController<T> {
    var cardControllers: MutableList<Pair<Any?, CardController<T>>> = mutableListOf()

    fun swipeRight() {
        cardControllers.firstOrNull { (_, v) ->
            v.cardX == 0F && v.cardY == 0F
        }?.second?.swipeRight()
    }

    fun swipeLeft() {
        cardControllers.firstOrNull { (_, v) ->
            v.cardX == 0F && v.cardY == 0F
        }?.second?.swipeLeft()
    }
}