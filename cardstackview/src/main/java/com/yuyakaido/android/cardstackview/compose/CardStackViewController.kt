package com.yuyakaido.android.cardstackview.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> rememberCardStackViewController(): CardStackViewControllerType<T> {
    return remember { CardStackViewController() }
}

interface CardStackViewControllerType<T> {
    fun setControllers(controllers: List<Pair<Any?, CardControllerType<T>>>)
    fun currentCardController(key: Any?): CardControllerType<T>
    fun swipeRight()
    fun swipeLeft()
}

class CardStackViewController<T> : CardStackViewControllerType<T> {
    private var cardControllers: List<Pair<Any?, CardControllerType<T>>> = mutableListOf()

    override fun setControllers(controllers: List<Pair<Any?, CardControllerType<T>>>) {
        cardControllers = controllers
    }

    override fun currentCardController(key: Any?): CardControllerType<T> {
        return cardControllers.first { (k, _) ->
            k == key
        }.second
    }

    override fun swipeRight() {
        cardControllers.first { (_, v) ->
            v.cardX == 0F && v.cardY == 0F
        }.second.swipeRight()
    }

    override fun swipeLeft() {
        cardControllers.first { (_, v) ->
            v.cardX == 0F && v.cardY == 0F
        }.second.swipeLeft()
    }
}