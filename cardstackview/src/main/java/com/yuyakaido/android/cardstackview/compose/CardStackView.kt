package com.yuyakaido.android.cardstackview.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex

@Composable
fun <T> CardStackView(
    items: List<T>,
    modifier: Modifier = Modifier,
    config: CardStackConfig = CardStackConfig(),
    controller: CardStackViewControllerType<T> = rememberCardStackViewController(items, config),
    onDrag: (T, Float) -> Unit = { _, _ -> },
    onDragStart: (T, Offset) -> Unit = { _, _ -> },
    onDragEnd: (T) -> Unit = {},
    onDragCancel: (T) -> Unit = {},
    onEmpty: () -> Unit = {},
    onSwiped: (T, Direction) -> Unit = { _, _ -> },
    content: @Composable (T) -> Unit
) {
    val visibleContents = items.filter {
        !controller.currentCardController(it).isCardSwiped()
    }.take(config.visibleCount)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val visibleSize = visibleContents.size
        val zIndexes = remember { List(visibleSize) { mutableStateOf(visibleSize - it - 1) } }
        visibleContents.forEachIndexed { index, item ->
            val zIndex = zIndexes[index].value
            val cardController = controller.currentCardController(item)
            key(item, cardController) {
                val padding = PaddingBetweenCards.get(config.translationInterval, config.stackFrom)
                val paddingX by animateFloatAsState(targetValue = (zIndex * padding.paddingX))
                val paddingY by animateFloatAsState(targetValue = (zIndex * padding.paddingY))
                Box(
                    modifier = modifier
                        .zIndex(zIndex.toFloat())
                        .graphicsLayer(
                            translationX = cardController.cardX + paddingX,
                            translationY = cardController.cardY + paddingY,
                            rotationZ = cardController.rotation,
                        )
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    onDragStart(item, it)
                                },
                                onDragEnd = {
                                    cardController.onDragEnd()
                                    onDragEnd(item)
                                },
                                onDragCancel = {
                                    cardController.onDragCancel()
                                    onDragCancel(item)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    cardController.onDrag(dragAmount)
                                    onDrag(item, cardController.ratio)
                                }
                            )
                        },
                ) {
                    content(item)
                }
                LaunchedEffect(cardController.direction) {
                    cardController.direction?.let {
                        onSwiped(item, it)
                    }
                }
            }
        }
        LaunchedEffect(controller.isEmpty()) {
            if (controller.isEmpty()) {
                onEmpty()
            }
        }
    }
}

internal data class PaddingBetweenCards(
    val paddingX: Float,
    val paddingY: Float,
) {
    companion object {
        fun get(translationInterval: Float, stackFrom: StackFrom): PaddingBetweenCards {
            return when (stackFrom) {
                StackFrom.None -> {
                    PaddingBetweenCards(0f, 0f)
                }
                StackFrom.Top -> {
                    PaddingBetweenCards(0f, translationInterval)
                }
                StackFrom.TopAndLeft -> {
                    PaddingBetweenCards(translationInterval, translationInterval)
                }
                StackFrom.TopAndRight -> {
                    PaddingBetweenCards(-translationInterval, translationInterval)
                }
                StackFrom.Bottom -> {
                    PaddingBetweenCards(0f, -translationInterval)
                }
                StackFrom.BottomAndLeft -> {
                    PaddingBetweenCards(translationInterval, -translationInterval)
                }
                StackFrom.BottomAndRight -> {
                    PaddingBetweenCards(-translationInterval, -translationInterval)
                }
                StackFrom.Left -> {
                    PaddingBetweenCards(translationInterval, 0f)
                }
                StackFrom.Right -> {
                    PaddingBetweenCards(-translationInterval, 0f)
                }
            }
        }
    }
}
