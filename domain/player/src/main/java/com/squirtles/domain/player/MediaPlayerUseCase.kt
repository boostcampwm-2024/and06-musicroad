package com.squirtles.domain.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.squirtles.core.mediaservice.MediaControllerProvider
import com.squirtles.core.mediaservice.SEEK_TO_DURATION
import com.squirtles.core.model.Pick
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*  */
class MediaPlayerUseCase @Inject constructor(
    private val mediaControllerProvider: MediaControllerProvider,
) {
    private var mediaController: MediaController? = null

    val audioSessionId = mediaControllerProvider.audioSessionFlow

    suspend fun readyPlayer() {
        mediaController = mediaControllerProvider.mediaControllerFlow.first()
    }

    fun addMediaItem(pick: Pick) {
        val mediaItem = pick.toMediaItem()
        mediaController?.addMediaItem(mediaItem)
    }

    fun addMediaItems(picks: List<Pick>) {
        mediaController?.run {
            addMediaItems(picks.map { it.toMediaItem() })
        }
    }

    fun setMediaItem(pick: Pick) {
        mediaController?.run {
            clearMediaItems()
            pause()
            setMediaItem(pick.toMediaItem())
            prepare()
            repeatMode = Player.REPEAT_MODE_OFF
            volume = 0.5f
        }
    }

    fun setMediaItems(picks: List<Pick>) {
        mediaController?.run {
            setMediaItems(picks.map {
                it.toMediaItem()
            })
            prepare()
            playWhenReady = false
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0.5f
        }
    }

    fun changeRepeatMode(repeatable: Boolean) {
        mediaController?.repeatMode = if (repeatable) {
            Player.REPEAT_MODE_ALL
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun stop() {
        mediaController?.stop()
    }

    fun release() {
        mediaController?.release()
    }

    fun next() {
        if (mediaController?.hasNextMediaItem() == true) {
            mediaController?.seekToNextMediaItem()
        }
    }

    fun previous() {
        if (mediaController?.hasPreviousMediaItem() == true) {
            mediaController?.seekToPreviousMediaItem()
        } else {
            mediaController?.seekToDefaultPosition()
        }
    }

    fun advanceBy() {
        mediaController?.apply {
            seekTo(currentPosition + SEEK_TO_DURATION)
        }
    }

    fun rewindBy() {
        mediaController?.apply {
            seekTo(currentPosition - SEEK_TO_DURATION)
        }
    }

    fun onSeekingStarted() {
        mediaController?.seekToDefaultPosition()
    }

    fun onSeekingFinished(time: Long) {
        mediaController?.seekTo(time)
    }

    private fun Pick.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(this.id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.songName)
                    .setArtist(song.artistName)
                    .setAlbumTitle(song.albumName)
                    .build()
            )
            .setUri(song.previewUrl)
            .build()
    }
}
