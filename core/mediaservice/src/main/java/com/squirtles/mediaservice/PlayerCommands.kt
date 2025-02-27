package com.squirtles.mediaservice

import android.os.Bundle
import androidx.media3.session.SessionCommand

private const val ACTION_SEEK_FORWARD = "action_seek_forward"
private const val ACTION_SEEK_REWIND = "action_seek_rewind"
private const val ACTION_PLAY_AND_PAUSE = "action_play_and_pause"

enum class PlayerCommands(
    val customAction: String,
    val displayName: String,
    val iconResId: (Boolean) -> Int,
    val sessionCommand: SessionCommand,
) {
    SEEK_REWIND(
        customAction = ACTION_SEEK_REWIND,
        displayName = "SeekRewind",
        iconResId = { androidx.media3.session.R.drawable.media3_icon_skip_back_5 },
        sessionCommand = SessionCommand(ACTION_SEEK_REWIND, Bundle.EMPTY)
    ),
    PLAY_AND_PAUSE(
        customAction = ACTION_PLAY_AND_PAUSE,
        displayName = "PlayPause",
        iconResId = { isPlaying ->
            if (isPlaying) {
                androidx.media3.session.R.drawable.media3_icon_pause
            } else {
                androidx.media3.session.R.drawable.media3_icon_play
            }
        },
        sessionCommand = SessionCommand(ACTION_PLAY_AND_PAUSE, Bundle.EMPTY)
    ),
    SEEK_FORWARD(
        customAction = ACTION_SEEK_FORWARD,
        displayName = "SeekForward",
        iconResId = { androidx.media3.session.R.drawable.media3_icon_skip_forward_5 },
        sessionCommand = SessionCommand(ACTION_SEEK_FORWARD, Bundle.EMPTY)
    ),
}
