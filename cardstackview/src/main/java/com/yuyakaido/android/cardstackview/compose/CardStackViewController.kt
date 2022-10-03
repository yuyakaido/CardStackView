package com.yuyakaido.android.cardstackview.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> rememberCardStackViewController(): CardStackViewController<T> {
    return remember { CardStackViewController() }
}

class CardStackViewController<T> {
    private var cardControllers: List<Pair<Any?, CardController<T>>> = mutableListOf()

    fun setControllers(controllers: List<Pair<Any?, CardController<T>>>) {
        cardControllers = controllers
    }

    fun currentCardController(key: Any?): CardController<T> {
        return cardControllers.first { (k, _) ->
            k == key
        }.second
    }

    fun swipeRight() {
        cardControllers.first { (_, v) ->
            v.cardX == 0F && v.cardY == 0F
        }.second.swipeRight()
    }

    fun swipeLeft() {
        cardControllers.first { (_, v) ->
            v.cardX == 0F && v.cardY == 0F
        }.second.swipeLeft()
    }
}