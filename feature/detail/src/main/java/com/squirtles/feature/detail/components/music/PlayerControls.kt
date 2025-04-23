package com.squirtles.feature.detail.components.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward5
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay5
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.core.common.ui.theme.MusicRoadTheme
import com.squirtles.core.common.ui.theme.White
import com.squirtles.detail.R

@Composable
internal fun PlayerControls(
    isPlaying: Boolean,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
    ) {
        Box(
            modifier = modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onReplayClick
            ) {
                Icon(
                    imageVector = Icons.Default.Replay5,
                    contentDescription = stringResource(id = R.string.player_replay_description),
                    modifier = Modifier.size(64.dp),
                    tint = White
                )
            }
        }
        Box(
            modifier = modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onPauseToggle
            ) {
                Icon(
                    imageVector = if (isPlaying) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },
                    contentDescription = stringResource(id = R.string.player_play_pause_description),
                    modifier = Modifier.size(64.dp),
                    tint = White
                )
            }
        }

        Box(
            modifier = modifier
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onForwardClick
            ) {
                Icon(
                    imageVector = Icons.Default.Forward5,
                    contentDescription = stringResource(id = R.string.player_forward_description),
                    modifier = Modifier.size(64.dp),
                    tint = White
                )
            }
        }
    }
}

@Preview
@Composable
private fun PlayerControlsPreview() {
    MusicRoadTheme {
        PlayerControls(
            isPlaying = true,
            onReplayClick = {},
            onPauseToggle = {},
            onForwardClick = {})
    }
}
