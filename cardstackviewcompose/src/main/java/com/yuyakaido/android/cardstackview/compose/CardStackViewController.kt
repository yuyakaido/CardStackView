package com.yuyakaido.android.cardstackview.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> rememberCardStackViewController(
    items: List<T>,
    setting: CardStackSetting,
    contentKey: (T) -> Any? = { it }
): CardStackViewController<T> {
    val controllers = items.map { contentKey(it) to rememberCardController(setting) }
    return remember {
        CardStackViewController(
            cardControllers = controllers,
            config = setting,
            contentKey = contentKey,
        )
    }
}

interface CardStackViewControllerType<T> {
    fun currentCardController(key: T): CardControllerType
    fun swipeRight()
    fun swipeLeft()
    fun rewind()
    fun isEmpty(): Boolean
}

class CardStackViewController<T>(
    private val cardControllers: List<Pair<Any?, CardControllerType>>,
    private val config: CardStackSetting,
    private val contentKey: (T) -> Any? = { it }
) : CardStackViewControllerType<T> {

    override fun currentCardController(key: T): CardControllerType {
        return cardControllers.first { (k, _) ->
            k == contentKey(key)
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