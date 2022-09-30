package com.yuyakaido.android.cardstackview.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
    return remember {
        val swipeX = Animatable(0f)
        val swipeY = Animatable(0f)
        CardController(
            swipeX,
            swipeY,
            scope,
            screenWidth,
            swipeDuration,
            swipedThreshold,
            rotateConfiguration
        )
    }
}

open class CardController<T>(
    private val swipeX: Animatable<Float, AnimationVector1D>,
    private val swipeY: Animatable<Float, AnimationVector1D>,
    private val scope: CoroutineScope,
    private val screenWidth: Float,
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
            launch { swipeX.animateTo(swipeX.targetValue + dragAmount.x) }
            launch { swipeY.animateTo(swipeY.targetValue + dragAmount.y) }
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
            launch { swipeX.animateTo(0f) }
            launch { swipeY.animateTo(0f) }
        }
    }

    fun swipeRight() {
        scope.launch {
            direction = Direction.RIGHT
            swipeX.animateTo(screenWidth + (screenWidth / 2), tween(swipeDuration))
        }
    }

    fun swipeLeft() {
        scope.launch {
            direction = Direction.LEFT
            swipeX.animateTo(-(screenWidth + (screenWidth / 2)), tween(swipeDuration))
        }
    }
}