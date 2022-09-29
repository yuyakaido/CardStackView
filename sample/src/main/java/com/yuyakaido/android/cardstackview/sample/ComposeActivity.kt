package com.yuyakaido.android.cardstackview.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import com.yuyakaido.android.cardstackview.compose.CardStackView
import com.yuyakaido.android.cardstackview.compose.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val random = Random(System.currentTimeMillis())
        setContent {
            val items = MutableStateFlow(
                List(100) {
                    Item(
                        id = it,
                        color = Color(
                            red = random.nextInt(),
                            green = random.nextInt(),
                            blue = random.nextInt()
                        )
                    )
                }
            )
            CardStackView(
                items = items.collectAsState().value
            )
        }
    }

}