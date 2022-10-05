package com.yuyakaido.android.cardstackview.compose

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex

@Composable
fun <T> CardStackView(
    items: List<T>,
    modifier: Modifier = Modifier,
    controller: CardStackViewControllerType<T> = rememberCardStackViewController(items),
    content: @Composable (T) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val visibleSize = items.size
        val zIndexes = remember { List(visibleSize) { mutableStateOf(visibleSize - it - 1) } }
        items.forEachIndexed { index, item ->
            val zIndex = zIndexes[index].value
            val cardController = controller.currentCardController(item)
            key(item, cardController) {
                Box(
                    modifier = modifier
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
                                    cardController.onDragEnd(item)
                                },
                                onDragCancel = {
                                    cardController.onDragCancel(item)
                                    Log.d("CardStackView", "onDragCancel")
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    cardController.onDrag(dragAmount, item)
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
}
