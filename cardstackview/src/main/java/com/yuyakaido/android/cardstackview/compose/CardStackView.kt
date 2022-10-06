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
    controller: CardStackViewControllerType<T> = rememberCardStackViewController(items),
    visibleCount: Int = 3,
    paddingBetweenCards: Float = 20F,
    onDrag: (T, Offset) -> Unit = { _, _ -> },
    onDragStart: (T) -> Unit = {},
    onDragEnd: (T) -> Unit = {},
    onDragCancel: (T) -> Unit = {},
    onEmpty: (T) -> Unit = {},
    onSwiped: (T, Direction) -> Unit = { _, _ -> },
    content: @Composable (T) -> Unit
) {
    val visibleContents = items.filter {
        !controller.currentCardController(it).isCardSwiped()
    }.take(visibleCount)

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
                val paddingTop by animateFloatAsState(targetValue = (zIndex * paddingBetweenCards))
                Box(
                    modifier = modifier
                        .zIndex(zIndex.toFloat())
                        .graphicsLayer(
                            translationX = cardController.cardX,
                            translationY = cardController.cardY + paddingTop,
                            rotationZ = cardController.rotation,
                        )
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = {
                                    onDragStart(item)
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
                                    onDrag(item, dragAmount)
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    content(item)
                }
                LaunchedEffect(cardController.direction) {
                    cardController.direction?.let {
                        onSwiped(item, it)
                    }
                }
                LaunchedEffect(controller.isEmpty()) {
                    if (controller.isEmpty()) {
                        onEmpty(item)
                    }
                }
            }
        }
    }
}
