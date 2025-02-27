package com.squirtles.player

import androidx.media3.common.Player
import androidx.media3.common.Tracks
import com.squirtles.mediaservice.MediaControllerProvider
import com.squirtles.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/* 현재 플레이어에 이벤트 리스너 등록 -> 로딩,재생,seekbar 탐색, 재생시간 등 데이터를 UI데이터로 변환  */
class MediaPlayerListenerUseCase @Inject constructor(
    private val mediaControllerProvider: MediaControllerProvider
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var timerJob: Job? = null

    fun playerStateFlow() = callbackFlow {
        val mediaController = mediaControllerProvider.mediaControllerFlow.first()

        val currentPlayerState = MutableStateFlow(
            if (mediaController.currentMediaItem != null) {
                PlayerState(
                    id = mediaController.currentMediaItem!!.mediaId,
                    isLoading = mediaController.isLoading,
                    isPlaying = mediaController.isPlaying,
                    hasNext = mediaController.hasNextMediaItem(),
                    currentPosition = mediaController.currentPosition,
                    duration = mediaController.duration,
                    bufferPercentage = mediaController.bufferedPercentage
                )
            } else {
                PlayerState()
            }
        )

        val playerListener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                currentPlayerState.update {
                    it.copy(isPlaying = isPlaying)
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
//                currentPlayerState.update {
//                    it.copy(id = mediaController.currentMediaItem?.mediaId ?: "", isLoading = isLoading)
//                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    currentPlayerState.update {
                        it.copy(currentPosition = mediaController.currentPosition)
                    }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        mediaController.seekTo(0)
                        mediaController.pause()
                    }

                    Player.STATE_READY -> {
                        currentPlayerState.update {
                            it.copy(id = mediaController.currentMediaItem?.mediaId ?: "")
                        }
                    }
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                super.onTracksChanged(tracks)
                currentPlayerState.value = PlayerState()
            }
        }

        coroutineScope.launch {
            currentPlayerState
                .map { it.isLoading.not() && it.isPlaying }
                .distinctUntilChanged()
                .collect { isPlaying ->
                    if (isPlaying) {
                        timerJob = coroutineScope.launch {
                            val startDuration = mediaController.currentPosition
                            val maxDuration = mediaController.contentDuration

                            while (isActive && startDuration <= maxDuration) {
                                currentPlayerState.update {
                                    it.copy(
                                        currentPosition = mediaController.currentPosition,
                                        bufferPercentage = mediaController.bufferedPercentage
                                    )
                                }
                                delay(1000L)
                            }
                        }
                    } else {
                        timerJob?.cancel()
                        timerJob = null
                    }
                }
        }

        coroutineScope.launch {
            currentPlayerState
                .onEach { send(it) }
                .collect()
        }

        mediaController.addListener(playerListener)

        awaitClose {
            mediaController.removeListener(playerListener)
            coroutineScope.cancel()
        }
    }
}
