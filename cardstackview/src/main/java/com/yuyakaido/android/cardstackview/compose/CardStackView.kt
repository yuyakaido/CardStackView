package com.yuyakaido.android.cardstackview.compose

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlin.math.min

data class Item(
    val id: Int,
    val color: Color
)

@Composable
fun CardStackView(
    items: List<Item>
) {
    val cardWidthDp = 300.dp
    val cardHeightDp = 400.dp

    val visibleSize = items.size
    val zIndexes = remember { List(visibleSize) { mutableStateOf(visibleSize - it - 1) } }
    val translationXs = remember { List(visibleSize) { Animatable(0f) } }
    val translationYs = remember { List(visibleSize) { Animatable(0f) } }
    val scales = remember { List(visibleSize) { Animatable(1f - it * 0.05f) } }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        repeat(visibleSize) { index ->
            val item = items[index]
            val xIndex = zIndexes[index].value
            val translationX = translationXs[index]
            val translationY = translationYs[index]
            val currentScale = scales[index]
            val nextScale = scales.getOrNull(index + 1)
            Box(
                modifier = Modifier
                    .zIndex(xIndex.toFloat())
                    .graphicsLayer(
                        translationX = translationX.value,
                        translationY = translationY.value,
//                        scaleX = currentScale.value,
//                        scaleY = currentScale.value
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                Log.d("CardStackView", "onDragStart")
                            },
                            onDragEnd = {
                                Log.d("CardStackView", "onDragEnd")
                                Log.d("CardStackView", "TranslationX: ${translationX.velocityVector.value}")
                                Log.d("CardStackView", "TranslationY: ${translationY.velocityVector.value}")
                                val cardDistance = Offset(cardWidthDp.toPx(), cardHeightDp.toPx()).getDistance() / 2
                                Log.d("CardStackView", "CardDistance: $cardDistance")
                                val translationDistance = Offset(translationX.targetValue, translationY.targetValue).getDistance()
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
                            },
                            onDragCancel = {
                                Log.d("CardStackView", "onDragCancel")
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    translationX.animateTo(translationX.targetValue + dragAmount.x)
                                }
                                scope.launch {
                                    translationY.animateTo(translationY.targetValue + dragAmount.y)
                                }
                                scope.launch {
                                    val cardDistance = Offset(cardWidthDp.toPx(), cardHeightDp.toPx()).getDistance() / 2
                                    val translationDistance = Offset(translationX.targetValue, translationY.targetValue).getDistance()
                                    val percentage = min(1f, translationDistance / cardDistance)
                                    val proportion = 1f - percentage
                                    val targetScale = 1f - proportion * 0.05f
                                    nextScale?.animateTo(targetScale)
                                }
                            }
                        )
                    }
                    .size(
                        width = cardWidthDp,
                        height = cardHeightDp
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(item.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.id.toString(),
                    fontSize = 80.sp
                )
            }
        }
    }
}
