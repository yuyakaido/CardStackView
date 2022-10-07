package com.yuyakaido.android.cardstackview.sample

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.yuyakaido.android.cardstackview.compose.CardController
import com.yuyakaido.android.cardstackview.compose.DEFAULT_SWIPED_THRESHOLD
import com.yuyakaido.android.cardstackview.compose.RotateConfiguration
import kotlinx.coroutines.CoroutineScope

@Composable
fun <Spot> rememberCustomCardController(): CustomCardControllers<Spot> {
    val scope = rememberCoroutineScope()
    val screenWidth =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeight =
        with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val cardWidth = with(LocalDensity.current) { LocalView.current.width.dp.toPx() }
    val cardHeight = with(LocalDensity.current) { LocalView.current.height.dp.toPx() }

    return remember {
        val swipeX = Animatable(0f)
        val swipeY = Animatable(0f)
        val nextScale = Animatable(1F)
        CustomCardControllers(
            swipeX = swipeX,
            swipeY = swipeY,
            nextScale = nextScale,
            scope = scope,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            swipeDuration = 800,
            swipedThreshold = DEFAULT_SWIPED_THRESHOLD,
            rotateConfiguration = RotateConfiguration.default(),
        )
    }
}

class CustomCardControllers<Spot>(
    swipeX: Animatable<Float, AnimationVector1D>,
    swipeY: Animatable<Float, AnimationVector1D>,
    nextScale: Animatable<Float, AnimationVector1D>,
    scope: CoroutineScope,
    screenWidth: Float,
    screenHeight: Float,
    cardWidth: Float,
    cardHeight: Float,
    swipeDuration: Int,
    swipedThreshold: Float,
    rotateConfiguration: RotateConfiguration,
) : CardController<Spot>(
    swipeX = swipeX,
    swipeY = swipeY,
    nextScale = nextScale,
    scope = scope,
    screenWidth = screenWidth,
    screenHeight = screenHeight,
    cardWidth = cardWidth,
    cardHeight = cardHeight,
    swipeDuration = swipeDuration,
    swipedThreshold = swipedThreshold,
    rotateConfiguration = rotateConfiguration
) {
    fun swipeRight(spot: Spot) {
        swipeRight()
    }

    fun swipeLeft(spot: Spot) {
        swipeLeft()
    }
}