package com.squirtles.feature.detail.components

import android.util.Size
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.squirtles.core.common.ui.theme.White
import com.squirtles.core.common.ui.toImageUrlWithSize
import com.squirtles.feature.detail.R

@Composable
internal fun MusicVideoKnob(
    thumbnail: String,
    modifier: Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite repeatable")
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "infinite repeatable"
    )

    Surface(
        modifier = modifier
            .size(width = 16.dp, height = 320.dp)
            .offset(x = offsetX.dp),
        color = White,
        shape = RoundedCornerShape(topStart = 30.dp, bottomStart = 30.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumbnail.toImageUrlWithSize(Size(560, 320)))
                .build(),
            contentDescription = stringResource(R.string.pick_swipe_icon_description),
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp)
                .padding(start = 2.dp)
                .clip(RoundedCornerShape(topStart = 30.dp, bottomStart = 30.dp)),
            placeholder = ColorPainter(Color.Transparent),
            error = ColorPainter(Color.Transparent),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview
@Composable
private fun MusicVideoKnobPreview() {
    MusicVideoKnob(
        thumbnail = "",
        modifier = Modifier
    )
}
