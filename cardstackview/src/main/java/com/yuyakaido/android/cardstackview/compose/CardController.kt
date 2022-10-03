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

enum class Direction {
    LEFT, RIGHT,
}

enum class RotateRatio(val value: Int) {
    LARGE(30),
    MEDIUM(60),
    SMALL(90);
}

data class RotateConfiguration(
    val ratio: RotateRatio,
    val tilt: Float,
) {
    companion object {
        fun default(): RotateConfiguration {
            return RotateConfiguration(
                ratio = RotateRatio.MEDIUM,
                tilt = 40F
            )
        }
    }
}

const val DEFAULT_SWIPE_DURATION = 500
const val DEFAULT_SWIPED_THRESHOLD = 3F

@Composable
fun <T> rememberCardController(
    swipeDuration: Int = DEFAULT_SWIPE_DURATION,
    swipedThreshold: Float = DEFAULT_SWIPED_THRESHOLD,
    rotateConfiguration: RotateConfiguration = RotateConfiguration.default()
): CardController<T> {
    val scope = rememberCoroutineScope()
    val screenWidth =
        with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val cardWidth = with(LocalDensity.current) { LocalView.current.width.dp.toPx() }
    val cardHeight = with(LocalDensity.current) { LocalView.current.height.dp.toPx() }

    return remember {
        val swipeX = Animatable(0f)
        val swipeY = Animatable(0f)
        val nextScale = Animatable(1F)
        CardController(
            swipeX,
            swipeY,
            nextScale,
            scope,
            screenWidth,
            cardWidth,
            cardHeight,
            swipeDuration,
            swipedThreshold,
            rotateConfiguration
        )
    }
}

open class CardController<T>(
    private val swipeX: Animatable<Float, AnimationVector1D>,
    private val swipeY: Animatable<Float, AnimationVector1D>,
    private val nextScale: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope,
    private val screenWidth: Float,
    private val cardWidth: Float,
    private val cardHeight: Float,
    private val swipeDuration: Int,
    private val swipedThreshold: Float,
    private val rotateConfiguration: RotateConfiguration,
) {
    val cardX: Float
        get() = swipeX.value

    val cardY: Float
        get() = swipeY.value

    val rotation: Float
        get() = (swipeX.value / rotateConfiguration.ratio.value)
            .coerceIn(
                -rotateConfiguration.tilt,
                rotateConfiguration.tilt
            )

    var direction: Direction? = null

    fun onDrag(dragAmount: Offset) {
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

    fun onDragEnd() {
        val isSwiped = abs(swipeX.targetValue) > abs(screenWidth) / swipedThreshold
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


    fun onDragCancel() {
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

    fun isCardSwiped(): Boolean {
        return abs(swipeX.value) == screenWidth
    }

    fun swipeRight() {
        scope.launch {
            direction = Direction.RIGHT
            swipeX.animateTo(
                targetValue = screenWidth * 2,
                animationSpec = tween(swipeDuration)
            )
        }
    }

    fun swipeLeft() {
        scope.launch {
            direction = Direction.LEFT
            swipeX.animateTo(
                targetValue = -(screenWidth * 2),
                animationSpec = tween(swipeDuration)
            )
        }
    }
}