package com.squirtles.mediaservice

import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

const val SEEK_TO_DURATION = 5_000L

@OptIn(UnstableApi::class)
internal class CustomMediaSessionCallback : MediaSession.Callback {

    /* MediaSession이 MediaController의 연결 요청을 수락할 때 사용할 수 있는 명령어 집합을 구성하고 반환 */
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult =

        // 컨트롤러가 미디어 알림과 연관된 컨트롤러인지 확인
        if (session.isMediaNotificationController(controller)) {
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
            var customCommands = PlayerCommands.entries.toList()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // 안드로이드 14 (API 34) 이상
                customCommands = customCommands.reversed()
            }

            customCommands.forEach { commandButton ->
                commandButton.sessionCommand.let {
                    it
                    availableSessionCommands.add(it)
                }
            }

            MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(availableSessionCommands.build())
                .setCustomLayout(
                    customCommands
                        .filter {
                            it.customAction == PlayerCommands.SEEK_REWIND.customAction
                                    || it.customAction == PlayerCommands.SEEK_FORWARD.customAction
                        }.map {
                            CommandButton.Builder()
                                .setDisplayName(it.displayName)
                                .setIconResId(it.iconResId(session.player.isPlaying))
                                .setSessionCommand(it.sessionCommand)
                                .build()
                        }
                )
                .build()
        } else {
            MediaSession.ConnectionResult.AcceptedResultBuilder(session).build()
        }

    // 사용자 정의 명령이 수신되었을 때 호출되는 콜백
    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {

        when (customCommand.customAction) {
            PlayerCommands.SEEK_REWIND.customAction -> {
                session.player.run {
                    seekTo(currentPosition - SEEK_TO_DURATION)
                }
            }

            PlayerCommands.PLAY_AND_PAUSE.customAction -> {
                if (!session.player.isPlaying) session.player.play()
                else session.player.pause()
            }

            PlayerCommands.SEEK_FORWARD.customAction -> {
                session.player.run {
                    seekTo(currentPosition + SEEK_TO_DURATION)
                }
            }

            else -> {
                // Do nothing
            }
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }
}
