package com.squirtles.detail.components.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.common.ui.theme.Gray
import com.squirtles.common.ui.theme.MusicRoadTheme
import java.util.concurrent.TimeUnit

@Composable
internal fun PlayBar(
    duration: Long,
    currentTime: Long,
    bufferPercentage: Int,
    isPlaying: Boolean,
    onSeekChanged: (timeMs: Float) -> Unit,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            PlayProgressIndicator(
                currentTime = currentTime.toFloat(),
                duration = duration.toFloat(),
                bufferPercentage = bufferPercentage.toFloat(),
                onSeekChanged = onSeekChanged
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentTime.formatMinSec(),
                color = Gray
            )
            Box(modifier = Modifier.weight(2f)) {
                PlayerControls(
                    modifier = Modifier.fillMaxWidth(),
                    isPlaying = isPlaying,
                    onReplayClick = onReplayClick,
                    onPauseToggle = onPauseToggle,
                    onForwardClick = onForwardClick
                )
            }
            Text(
                text = duration.formatMinSec(),
                color = Gray
            )
        }
    }
}

private fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "00:00"
    } else {
        val totalSeconds =
            TimeUnit.MILLISECONDS.toSeconds(this) + (this % 1000 / 500) // 500ms 이상일 경우 반올림
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}

@Preview
@Composable
private fun PlayBarPreview() {
    MusicRoadTheme {
        PlayBar(
            modifier = Modifier.fillMaxWidth(),
            duration = 10000L,
            currentTime = 5000L,
            bufferPercentage = 50,
            onSeekChanged = {},
            isPlaying = true,
            onReplayClick = {},
            onPauseToggle = {},
            onForwardClick = {}
        )
    }
}
