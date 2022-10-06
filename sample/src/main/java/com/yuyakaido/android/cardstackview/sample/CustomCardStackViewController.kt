package com.yuyakaido.android.cardstackview.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yuyakaido.android.cardstackview.compose.CardStackViewControllerType

@Composable
fun <Spot> rememberCustomCardStackViewController(): CustomCardStackViewController<Spot> {
    return remember {
        CustomCardStackViewController()
    }
}

class CustomCardStackViewController<Spot> : CardStackViewControllerType<Spot> {
    private var cardControllers: List<Pair<Spot, CustomCardControllers<Spot>>> = mutableListOf()

    fun setControllers(controllers: List<Pair<Spot, CustomCardControllers<Spot>>>) {
        cardControllers = controllers
    }

    override fun currentCardController(key: Spot): CustomCardControllers<Spot> {
        return cardControllers.first { (k, _) ->
            k == key
        }.second
    }

    override fun swipeRight() {
        val pair = cardControllers.firstOrNull { (_, v) ->
            !v.isCardSwiped()
        }
        pair?.second?.swipeRight(pair.first)
    }

    override fun swipeLeft() {
        val pair = cardControllers.firstOrNull { (_, v) ->
            !v.isCardSwiped()
        }
        pair?.second?.swipeLeft(pair.first)
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