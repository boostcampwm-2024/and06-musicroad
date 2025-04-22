package com.squirtles.musicroad.detail.components.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.squirtles.common.ui.Constants.DEFAULT_PADDING
import com.squirtles.common.ui.theme.PlayerBackground
import com.squirtles.model.PlayerState
import com.squirtles.model.Song

@Composable
fun MusicPlayer(
    song: Song,
    playerState: PlayerState,
    onSeekChanged: (Long) -> Unit,
    onReplayForwardClick: (Boolean) -> Unit,
    onPauseToggle: (Song) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = DEFAULT_PADDING)
            .background(
                color = PlayerBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 30.dp, vertical = DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PlayBar(
            duration = playerState.duration,
            currentTime = playerState.currentPosition,
            bufferPercentage = playerState.bufferPercentage,
            isPlaying = playerState.isPlaying,
            onSeekChanged = { timeMs ->
                onSeekChanged(timeMs.toLong())
            },
            onReplayClick = {
                onReplayForwardClick(false)
            },
            onPauseToggle = {
                onPauseToggle(song)
            },
            onForwardClick = {
                onReplayForwardClick(true)
            },
        )
    }
}
