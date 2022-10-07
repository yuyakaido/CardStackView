package com.yuyakaido.android.cardstackview.sample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yuyakaido.android.cardstackview.compose.CardStackView
import com.yuyakaido.android.cardstackview.compose.rememberCardStackViewController
import kotlinx.coroutines.launch

class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spots = createSpots()
        setContent {
            //CardStackViewSample(spots = spots)
            CustomCardStackViewSample(spots = spots)
        }
    }

    private fun createSpots(): List<Spot> {
        val spots = ArrayList<Spot>()
        spots.add(
            Spot(
                name = "Yasaka Shrine",
                city = "Kyoto",
                url = "https://source.unsplash.com/Xq1ntWruZQI/600x800"
            )
        )
        spots.add(
            Spot(
                name = "Fushimi Inari Shrine",
                city = "Kyoto",
                url = "https://source.unsplash.com/NYyCqdBOKwc/600x800"
            )
        )
        spots.add(
            Spot(
                name = "Bamboo Forest",
                city = "Kyoto",
                url = "https://source.unsplash.com/buF62ewDLcQ/600x800"
            )
        )
        spots.add(
            Spot(
                name = "Brooklyn Bridge",
                city = "New York",
                url = "https://source.unsplash.com/THozNzxEP3g/600x800"
            )
        )
        spots.add(
            Spot(
                name = "Empire State Building",
                city = "New York",
                url = "https://source.unsplash.com/USrZRcRS2Lw/600x800"
            )
        )
        spots.add(
            Spot(
                name = "The statue of Liberty",
                city = "New York",
                url = "https://source.unsplash.com/PeFk7fzxTdk/600x800"
            )
        )
        spots.add(
            Spot(
                name = "Louvre Museum",
                city = "Paris",
                url = "https://source.unsplash.com/LrMWHKqilUw/600x800"
            )
        )
        spots.add(
            Spot(
                name = "Eiffel Tower",
                city = "Paris",
                url = "https://source.unsplash.com/HN-5Z6AmxrM/600x800"
            )
        )
        spots.add(
            Spot(
                name = "Big Ben",
                city = "London",
                url = "https://source.unsplash.com/CdVAUADdqEc/600x800"
            )
        )
        spots.add(
            Spot(
                name = "Great Wall of China",
                city = "China",
                url = "https://source.unsplash.com/AWh9C-QjhE4/600x800"
            )
        )
        return spots
    }
}

private fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

@Composable
private fun CardStackViewSample(
    spots: List<Spot>,
) {
    val scope = rememberCoroutineScope()
    val cardStackController = rememberCardStackViewController(spots)

    Box(modifier = Modifier.fillMaxSize()) {
        CardStackView(
            items = spots,
            controller = cardStackController,
            modifier = Modifier
        ) {
            Spot(
                spot = it,
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        ButtonsSection(
            onLikeClick = {
                scope.launch {
                    cardStackController.swipeLeft()
                }
            },
            onRewindClick = {
                scope.launch {
                    cardStackController.rewind()
                }
            },
            onSkipClick = {
                scope.launch {
                    cardStackController.swipeRight()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
private fun CustomCardStackViewSample(
    spots: List<Spot>,
) {
    val scope = rememberCoroutineScope()
    val cardStackController = rememberCustomCardStackViewController<Spot>()
    val controllers = spots.map {
        it to rememberCustomCardController<Spot>()
    }
    val context = LocalContext.current
    cardStackController.setControllers(controllers)

    Box(modifier = Modifier.fillMaxSize()) {
        CardStackView(
            items = spots,
            controller = cardStackController,
            onSwiped = { item, direction ->
                showToast(context, "${item.name} Swipe To ${direction.name}")
            },
            onEmpty = {
                showToast(context, "Empty!!")
            },
            modifier = Modifier
        ) {
            Spot(
                spot = it,
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        ButtonsSection(
            onLikeClick = {
                scope.launch {
                    cardStackController.swipeLeft()
                }
            },
            onRewindClick = {
                scope.launch {
                    cardStackController.rewind()
                }
            },
            onSkipClick = {
                scope.launch {
                    cardStackController.swipeRight()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ButtonsSection(
    onLikeClick: () -> Unit,
    onRewindClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CircleButton(
            onClick = onSkipClick,
            icon = ImageVector.vectorResource(id = R.drawable.skip_red_24dp)
        )
        CircleButton(
            onClick = onRewindClick,
            icon = ImageVector.vectorResource(id = R.drawable.rewind_blue_24dp)
        )
        CircleButton(
            onClick = onLikeClick,
            icon = ImageVector.vectorResource(id = R.drawable.like_green_24dp)
        )
    }
}

@Composable
private fun Spot(
    modifier: Modifier = Modifier,
    spot: Spot
) {
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            model = spot.url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.6F)
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomStart)
        ) {
            Text(
                text = spot.name,
                fontSize = 26.sp,
                color = Color.White,
                fontWeight = FontWeight.W700,
            )
            Text(
                text = spot.city,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.W700,
            )
        }
    }
}

@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape)
            .size(60.dp),
        onClick = onClick,
    ) {
        Icon(
            icon, null,
        )
    }
}


@Preview
@Composable
private fun PreviewSpot() {
    Spot(
        spot = Spot(
            name = "Yasaka Shrine",
            city = "Kyoto",
            url = "https://source.unsplash.com/Xq1ntWruZQI/600x800"
        ),
        modifier = Modifier.fillMaxSize(),
    )
}