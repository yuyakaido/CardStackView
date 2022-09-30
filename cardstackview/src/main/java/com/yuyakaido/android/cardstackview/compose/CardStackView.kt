package com.yuyakaido.android.cardstackview.compose

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
inline fun <T> CardStackView(
    items: List<T>,
    modifier: Modifier = Modifier,
    controller: CardStackViewController<T> = rememberCardStackViewController(),
    contentKey: (target: T) -> Any? = { it },
    crossinline content: @Composable (T) -> Unit
) {
    val cardWidthDp = LocalView.current.width.dp
    val cardHeightDp = LocalView.current.height.dp

    val visibleSize = items.size
    val zIndexes = remember { List(visibleSize) { mutableStateOf(visibleSize - it - 1) } }
    val translationXs = remember { List(visibleSize) { Animatable(0f) } }
    val translationYs = remember { List(visibleSize) { Animatable(0f) } }
    val scales = remember { List(visibleSize) { Animatable(1f - it * 0.05f) } }
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        repeat(visibleSize) { index ->
            val item = items[index]
            val zIndex = zIndexes[index].value
            val translationX = translationXs[index]
            val translationY = translationYs[index]
            val currentScale = scales[index]
            val nextScale = scales.getOrNull(index + 1)
            val cardController = rememberCardController<T>()
            controller.cardControllers.add(contentKey(item) to cardController)

            Box(
                modifier = Modifier
                    .zIndex(zIndex.toFloat())
                    .graphicsLayer(
                        translationX = cardController.cardX,
                        translationY = cardController.cardY,
                        rotationZ = cardController.rotation,
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                Log.d("CardStackView", "onDragStart")
                            },
                            onDragEnd = {
                                Log.d("CardStackView", "onDragEnd")
                                Log.d(
                                    "CardStackView",
                                    "TranslationX: ${translationX.velocityVector.value}"
                                )
                                Log.d(
                                    "CardStackView",
                                    "TranslationY: ${translationY.velocityVector.value}"
                                )
                                cardController.onDragEnd()
                                /*
                                val cardDistance = Offset(
                                    cardWidthDp.toPx(),
                                    cardHeightDp.toPx()
                                ).getDistance() / 2
                                Log.d("CardStackView", "CardDistance: $cardDistance")
                                val translationDistance = Offset(
                                    translationX.targetValue,
                                    translationY.targetValue
                                ).getDistance()
                                Log.d("CardStackView", "TranslationDistance: $translationDistance")
                                val percentage = min(1f, translationDistance / cardDistance)
                                if (percentage < 0.5f) {
                                    scope.launch {
                                        translationX.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                    scope.launch {
                                        translationY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                    scope.launch {
                                        nextScale?.animateTo(
                                            targetValue = 1f - 1 * 0.05f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
                                    }
                                } else {
                                    scope.launch {
                                        translationX.animateTo(
                                            targetValue = translationX.velocityVector.value,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
//                                        translationXs.forEach { it.snapTo(0f) }
                                    }
                                    scope.launch {
                                        translationY.animateTo(
                                            targetValue = translationY.velocityVector.value,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        )
//                                        translationYs.forEach { it.snapTo(0f) }
                                    }
                                }
                                */
                            },
                            onDragCancel = {
                                cardController.onDragCancel()
                                Log.d("CardStackView", "onDragCancel")
                            },
                            onDrag = { change, dragAmount ->
                                cardController.onDrag(dragAmount)
                                /*
                                change.consume()
                                scope.launch {
                                    translationX.animateTo(translationX.targetValue + dragAmount.x)
                                }
                                scope.launch {
                                    translationY.animateTo(translationY.targetValue + dragAmount.y)
                                }
                                scope.launch {
                                    val cardDistance = Offset(
                                        cardWidthDp.toPx(),
                                        cardHeightDp.toPx()
                                    ).getDistance() / 2
                                    val translationDistance = Offset(
                                        translationX.targetValue,
                                        translationY.targetValue
                                    ).getDistance()
                                    val percentage = min(1f, translationDistance / cardDistance)
                                    val proportion = 1f - percentage
                                    val targetScale = 1f - proportion * 0.05f
                                    nextScale?.animateTo(targetScale)
                                }
                                */
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                content(item)
            }
        }
    }
}
