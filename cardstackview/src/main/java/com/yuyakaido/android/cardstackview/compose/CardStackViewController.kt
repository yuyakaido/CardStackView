package com.yuyakaido.android.cardstackview.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> rememberCardStackViewController(items: List<T>): CardStackViewController<T> {
    val controllers = items.map { it to rememberCardController<T>() }
    return remember {
        CardStackViewController(
            cardControllers = controllers,
        )
    }
}

interface CardStackViewControllerType<T> {
    fun currentCardController(key: T): CardControllerType<T>
    fun swipeRight()
    fun swipeLeft()
    fun rewind()
    fun isEmpty(): Boolean
}

class CardStackViewController<T>(
    private val cardControllers: List<Pair<T, CardControllerType<T>>>
) : CardStackViewControllerType<T> {

    override fun currentCardController(key: T): CardControllerType<T> {
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

    override fun isEmpty(): Boolean {
        return cardControllers.firstOrNull { (_, v) ->
            !v.isCardSwiped()
        } == null
    }
}