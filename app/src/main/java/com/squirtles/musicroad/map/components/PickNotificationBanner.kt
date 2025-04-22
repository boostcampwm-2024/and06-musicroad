package com.squirtles.musicroad.map.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.common.ui.theme.Black
import com.squirtles.common.ui.theme.MusicRoadTheme
import com.squirtles.common.ui.theme.White
import com.squirtles.model.Pick
import com.squirtles.musicroad.R
import com.squirtles.musicroad.detail.DetailViewModel.Companion.DEFAULT_PICK

@Composable
fun PickNotificationBanner(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    nearPicks: List<Pick>,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite repeatable")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "infinite repeatable"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .offset(y = offsetY.dp),
    ) {
        Text(
            text = stringResource(
                id = if (isPlaying) R.string.map_pick_notification_stop else R.string.map_pick_notification_play,
                nearPicks.count()
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (LocalConfiguration.current.screenHeightDp * 0.08).dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable {
                    onClick()
                }
                .background(White.copy(alpha = 0.8f))
                .padding(vertical = 10.dp, horizontal = 23.dp),
            color = Black,
        )
    }
}

@Preview
@Composable
private fun PickNotificationBannerPreview() {
    MusicRoadTheme {
        PickNotificationBanner(
            nearPicks = listOf(DEFAULT_PICK),
            onClick = { }
        )
    }
}
