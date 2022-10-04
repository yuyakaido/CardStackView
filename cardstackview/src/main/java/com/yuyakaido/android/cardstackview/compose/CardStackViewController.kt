package com.yuyakaido.android.cardstackview.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> rememberCardStackViewController(): CardStackViewController<T> {
    return remember { CardStackViewController() }
}

interface CardStackViewControllerType<T> {
    fun currentCardController(key: Any?): CardControllerType<T>
    fun swipeRight()
    fun swipeLeft()
    fun rewind()
}

class CardStackViewController<T> : CardStackViewControllerType<T> {
    private var cardControllers: List<Pair<Any?, CardControllerType<T>>> = mutableListOf()

    fun setControllers(controllers: List<Pair<Any?, CardControllerType<T>>>) {
        cardControllers = controllers
    }

    override fun currentCardController(key: Any?): CardControllerType<T> {
        return cardControllers.first { (k, _) ->
            k == key
        }.second
    }

    override fun swipeRight() {
        cardControllers.firstOrNull { (_, v) ->
            !v.isCardSwiped()
        }?.second?.swipeRight()
    }

    override fun swipeLeft() {
        cardControllers.firstOrNull { (_, v) ->
            !v.isCardSwiped()
        }?.second?.swipeLeft()
    }

    override fun rewind() {
        cardControllers.lastOrNull { (_, v) ->
            v.isCardSwiped()
        }?.second?.rewind()
    }
}