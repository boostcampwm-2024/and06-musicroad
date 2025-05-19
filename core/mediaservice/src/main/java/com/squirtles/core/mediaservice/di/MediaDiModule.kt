package com.squirtles.core.mediaservice.di

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.squirtles.core.mediaservice.CustomMediaSessionCallback
import com.squirtles.core.mediaservice.MediaControllerProvider
import com.squirtles.core.mediaservice.MediaControllerProviderImpl
import com.squirtles.core.mediaservice.MediaNotificationProvider
import com.squirtles.core.mediaservice.MediaNotificationProviderImpl
import com.squirtles.core.mediaservice.MediaPlayerService
import com.google.common.util.concurrent.ListenableFuture
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class MediaServiceBinds {

    @Binds
    abstract fun bindsMediaNotificationProvider(
        mediaNotification: MediaNotificationProviderImpl
    ): MediaNotificationProvider

    @OptIn(UnstableApi::class)
    @Binds
    abstract fun bindsMediaControllerProvider(
        mediaControllerProvider: MediaControllerProviderImpl
    ): MediaControllerProvider
}

@Module
@InstallIn(SingletonComponent::class)
object MediaServiceModule {

    @Singleton
    @Provides
    fun providesExoPlayer(
        @ApplicationContext context: Context,
    ): ExoPlayer =
        ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()

    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun provideAudioSessionId(
        exoPlayer: ExoPlayer
    ): Int = exoPlayer.audioSessionId

    @Singleton
    @Provides
    fun providesMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): MediaSession =
        MediaSession.Builder(context, player)
            .setCallback(CustomMediaSessionCallback())
            .build()

    @Singleton
    @Provides
    fun providesMediaNotificationManager(
        @ApplicationContext context: Context,
        mediaSession: MediaSession,
    ): MediaNotificationProviderImpl =
        MediaNotificationProviderImpl(context, mediaSession)

    @Singleton
    @Provides
    fun providesSessionToken(
        @ApplicationContext context: Context
    ): SessionToken {
        val sessionToken = SessionToken(context, ComponentName(context, MediaPlayerService::class.java))
        return sessionToken
    }


    @Singleton
    @Provides
    fun providesListenableFutureMediaController(
        @ApplicationContext context: Context,
        sessionToken: SessionToken
    ): ListenableFuture<MediaController> =
        MediaController
            .Builder(context, sessionToken)
            .buildAsync()
}
