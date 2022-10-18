package com.yuyakaido.android.cardstackview.compose

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Float.min
import kotlin.math.abs

interface CardControllerType {
    fun onDrag(dragAmount: Offset)
    fun onDragEnd()
    fun onDragCancel()
    fun isCardSwiped(): Boolean
    fun swipeLeft()
    fun swipeRight()
    fun rewind()

    val cardX: Float
    val cardY: Float
    val rotation: Float
    var direction: Direction?

    val ratio: Float
}

@Composable
fun rememberCardController(
    config: CardStackConfig,
): CardController {
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
        val nextScale = Animatable(1f)
        CardController(
            swipeX = swipeX,
            swipeY = swipeY,
            nextScale = nextScale,
            scope = scope,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            config = config,
        )
    }
}

open class CardController(
    private val swipeX: Animatable<Float, AnimationVector1D>,
    private val swipeY: Animatable<Float, AnimationVector1D>,
    private val nextScale: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope,
    private val screenWidth: Float,
    private val screenHeight: Float,
    private val cardWidth: Float,
    private val cardHeight: Float,
    private val config: CardStackConfig,
) : CardControllerType {
    override val cardX: Float
        get() = swipeX.value

    override val cardY: Float
        get() = swipeY.value

    override val rotation: Float
        get() = (swipeX.value / config.rotateRatio)
            .coerceIn(
                -config.maxDegree,
                config.maxDegree
            )

    override var direction: Direction? = null

    override val ratio: Float
        get() {
            val absDx = abs(cardX)
            val absDy = abs(cardY)
            val ratio: Float = if (absDx < absDy) {
                absDy / (screenHeight / 2.0f)
            } else {
                absDx / (screenWidth / 2.0f)
            }
            return min(ratio, 1.0f)
        }

    override fun onDrag(dragAmount: Offset) {
        scope.apply {
            launch {
                swipeX.animateTo(swipeX.targetValue + dragAmount.x)
            }
            launch {
                swipeY.animateTo(swipeY.targetValue + dragAmount.y)
            }
            launch {
                val cardDistance = Offset(
                    cardWidth,
                    cardHeight
                ).getDistance() / 2
                val translationDistance = Offset(
                    swipeX.targetValue,
                    swipeY.targetValue
                ).getDistance()
                val percentage = min(1f, translationDistance / cardDistance)
                val proportion = 1f - percentage
                val targetScale = 1f - proportion * 0.05f
                nextScale.animateTo(targetScale)
            }
        }
    }

    override fun onDragEnd() {
        val isSwiped = abs(swipeX.targetValue) / abs(screenWidth) > config.swipeThreshold
        if (isSwiped) {
            if (swipeX.targetValue > 0) {
                swipeRight()
            } else {
                swipeLeft()
            }
        } else {
            onDragCancel()
        }
    }


    override fun onDragCancel() {
        scope.apply {
            launch {
                swipeX.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                swipeY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                nextScale.animateTo(
                    targetValue = 1f - 1 * 0.05f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }

    override fun isCardSwiped(): Boolean {
        return abs(swipeX.value) >= screenWidth
    }

    override fun swipeRight() {
        scope.launch {
            direction = Direction.Right
            swipeX.animateTo(
                targetValue = screenWidth * 2,
                animationSpec = tween(config.swipeDuration)
            )
        }
    }

    override fun swipeLeft() {
        scope.launch {
            direction = Direction.Left
            swipeX.animateTo(
                targetValue = -(screenWidth * 2),
                animationSpec = tween(config.swipeDuration)
            )
        }
    }

    override fun rewind() {
        scope.apply {
            direction = null
            launch {
                swipeX.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                swipeY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                nextScale.animateTo(
                    targetValue = 0.95f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
        }
    }
}