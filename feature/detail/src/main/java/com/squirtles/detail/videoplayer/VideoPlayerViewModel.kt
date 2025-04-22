package com.squirtles.detail.videoplayer

import android.content.Context
import android.util.Size
import android.view.Surface
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.squirtles.detail.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor() : ViewModel() {

    private var player: ExoPlayer? = null

    private var _playerState = MutableStateFlow(VideoPlayerState.Playing) // 현재 플레이어의 상태
    val playerState = _playerState.asStateFlow()

    private var _videoSize = MutableStateFlow(Size(0, 0)) // 영상 크기
    val videoSize = _videoSize.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private var lastPosition = 0L

    fun initializePlayer(context: Context, url: String) {
        if (player != null) {
            setPlayer()
            return
        }

        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
            addListener(object : Player.Listener {
                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    viewModelScope.launch {
                        _videoSize.emit(Size(videoSize.width, videoSize.height))
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    viewModelScope.launch {
                        when (state) {
                            Player.STATE_IDLE,
                            Player.STATE_BUFFERING -> {
                                _isLoading.emit(true)
                            }

                            Player.STATE_READY -> {
                                _isLoading.emit(false)
                            }

                            Player.STATE_ENDED -> {
                                Toast.makeText(context, getString(context, R.string.video_player_ended_message), Toast.LENGTH_SHORT).show()
                                _playerState.emit(VideoPlayerState.Replay)
                                seekTo(0)
                            }
                        }
                    }
                }
            })
        }
        player = exoPlayer

        setPlayerSource(url)
    }

    fun play() {
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    private fun setPlayerSource(url: String) {
        player?.run {
            val mediaItem = MediaItem.fromUri(url)
            setMediaItem(mediaItem)
            prepare()
            seekTo(lastPosition)
        }
        setPlayer()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    fun setPlayState(playerState: VideoPlayerState) {
        viewModelScope.launch {
            _playerState.emit(playerState)
        }
    }

    fun setLastPosition() {
        lastPosition = player?.currentPosition ?: 0
    }

    fun setSurface(surface: Surface?) {
        player?.setVideoSurface(surface)
    }

    private fun setPlayer() {
        viewModelScope.launch {
            combine(isLoading, playerState) { isLoading, playState ->
                !isLoading && (playState == VideoPlayerState.Playing)
            }.collect { shouldPlay ->
                if (shouldPlay) {
                    player?.play()
                } else {
                    player?.pause()
                }
            }
        }
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }
}
