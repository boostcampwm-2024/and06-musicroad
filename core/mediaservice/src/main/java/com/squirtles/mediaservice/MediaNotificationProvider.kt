package com.squirtles.mediaservice

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import javax.inject.Inject

interface MediaNotificationProvider {

    @OptIn(UnstableApi::class)
    fun createMediaNotification(actionFactory: MediaNotification.ActionFactory): MediaNotification
}

class MediaNotificationProviderImpl @Inject constructor(
    private val context: Context,
    private val mediaSession: MediaSession
) : MediaNotificationProvider {

    @OptIn(UnstableApi::class)
    override fun createMediaNotification(actionFactory: MediaNotification.ActionFactory): MediaNotification {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        makeNotificationChannel(notificationManager)

        val mediaItem = mediaSession.player.currentMediaItem

        val notificationBuilder = NotificationCompat.Builder(
            context,
            NOTIFICATION_CHANNEL_ID.toString()
        ).apply {
            priority = NotificationCompat.PRIORITY_DEFAULT
            setSilent(true)
            setSmallIcon(R.drawable.ic_musicroad_foreground)
            setContentTitle(mediaItem?.mediaMetadata?.title)
            setContentIntent(createNotifyPendingIntent())
            setDeleteIntent(
                actionFactory.createMediaActionPendingIntent(
                    mediaSession,
                    Player.COMMAND_STOP.toLong()
                )
            )
            setStyle(
                MediaStyleNotificationHelper
                    .MediaStyle(mediaSession)
            )
            setOngoing(true)

            PlayerCommands.entries.forEach { commandButton ->
                addAction(
                    actionFactory.createCustomAction(
                        mediaSession,
                        IconCompat.createWithResource(
                            context,
                            commandButton.iconResId(mediaSession.player.isPlaying)
                        ),
                        commandButton.displayName,
                        commandButton.customAction,
                        commandButton.sessionCommand.customExtras
                    )
                )
            }
        }

        return MediaNotification(
            NOTIFICATION_CHANNEL_ID,
            notificationBuilder.build()
        )
    }

    /* 알림 누르면 이동할 intent 설정 */
    private fun createNotifyPendingIntent(): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent().apply {
                action = Intent.ACTION_VIEW
                component = ComponentName(context, TARGET_ACTIVITY)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    /* Notification Channel 생성 */
    @SuppressLint("ObsoleteSdkInt")
    private fun makeNotificationChannel(notificationManager: NotificationManager) {
        // Android 8.0 미만 버전에서는 Notification Channel을 생성할 필요가 없음 -> 앱 단일 Channel 가짐
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
            notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID.toString()) != null
        ) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID.toString(),
            "MediaPlayer",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = 100
        const val TARGET_ACTIVITY = "com.squirtles.musicroad.main.MainActivity"
    }
}
