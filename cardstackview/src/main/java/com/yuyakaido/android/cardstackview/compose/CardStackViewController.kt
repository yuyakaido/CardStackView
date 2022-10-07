package com.yuyakaido.android.cardstackview.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> rememberCardStackViewController(
    items: List<T>,
    config: CardStackConfig
): CardStackViewController<T> {
    val controllers = items.map { it to rememberCardController<T>(config) }
    return remember {
        CardStackViewController(
            cardControllers = controllers,
            config = config,
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
    private val cardControllers: List<Pair<T, CardControllerType<T>>>,
    private val config: CardStackConfig,
) : CardStackViewControllerType<T> {

    override fun currentCardController(key: T): CardControllerType<T> {
        return cardControllers.first { (k, _) ->
            k == key
        }.second
    }

    override fun swipeRight() {
        if (config.swipeMethod.canSwipeManually()) {
            cardControllers.firstOrNull { (_, v) ->
                !v.isCardSwiped()
            }?.second?.swipeRight()
        }
    }

    override fun swipeLeft() {
        if (config.swipeMethod.canSwipeManually()) {
            cardControllers.firstOrNull { (_, v) ->
                !v.isCardSwiped()
            }?.second?.swipeLeft()
        }
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