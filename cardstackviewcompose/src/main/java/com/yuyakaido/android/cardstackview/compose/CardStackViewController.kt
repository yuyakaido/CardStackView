package com.yuyakaido.android.cardstackview.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun <T> rememberCardStackViewController(
    items: List<T>,
    setting: CardStackSetting,
    contentKey: (T) -> Any? = { it }
): CardStackViewController<T> {
    var controllers: List<Pair<Any?, CardControllerType>> = remember {
        mutableListOf()
    }
    val keys = items.map { contentKey(it) }
    val newControllers = items.map { contentKey(it) to rememberCardController(setting) }
    controllers = (controllers.filterNot { keys.contains(it.first) } + newControllers)
        .sortedBy { if (it.second.isCardSwiped()) -1 else 1 }
    return remember(keys) {
        CardStackViewController(
            cardControllers = controllers,
            setting = setting,
            contentKey = contentKey,
        )
    }
}

interface CardStackViewControllerType<T> {
    fun setting(): CardStackSetting
    fun currentCardController(item: T): CardControllerType
    fun swipeRight()
    fun swipeLeft()
    fun rewind()
    fun isEmpty(): Boolean
    fun displayedItem(item: T): T?
}

class CardStackViewController<T>(
    private val cardControllers: List<Pair<Any?, CardControllerType>>,
    private val setting: CardStackSetting,
    private val contentKey: (T) -> Any? = { it }
) : CardStackViewControllerType<T> {

    override fun setting(): CardStackSetting {
        return setting
    }

    override fun currentCardController(item: T): CardControllerType {
        return cardControllers.first { (k, _) ->
            k == contentKey(item)
        }.second
    }

    override fun swipeRight() {
        if (setting.swipeMethod.canSwipeManually()) {
            cardControllers.firstOrNull { (_, v) ->
                !v.isCardSwiped()
            }?.second?.swipeRight(false)
        }
    }

    override fun swipeLeft() {
        if (setting.swipeMethod.canSwipeManually()) {
            cardControllers.firstOrNull { (_, v) ->
                !v.isCardSwiped()
            }?.second?.swipeLeft(false)
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

    override fun displayedItem(item: T): T? {
        val displayed = cardControllers.firstOrNull {
            !it.second.isCardSwiped()
        } ?: return null

        return if (displayed.first == contentKey(item)) {
            item
        } else {
            null
        }
    }
}