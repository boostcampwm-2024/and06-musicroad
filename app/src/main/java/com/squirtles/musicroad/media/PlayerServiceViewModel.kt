//package com.squirtles.musicroad.media
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.squirtles.domain.model.Pick
//import com.squirtles.domain.model.PlayerState
//import com.squirtles.domain.model.Song
//import com.squirtles.domain.player.MediaPlayerListenerUseCase
//import com.squirtles.domain.player.MediaPlayerUseCase
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.async
//import kotlinx.coroutines.flow.SharingStarted
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.stateIn
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class PlayerServiceViewModel @Inject constructor(
//    private val mediaPlayerUseCase: MediaPlayerUseCase,
//    private val mediaPlayerListenerUseCase: MediaPlayerListenerUseCase,
//) : ViewModel() {
//
//    val playerState: StateFlow<PlayerState> = mediaPlayerListenerUseCase.playerStateFlow()
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = PlayerState()
//        )
//
//    val audioSessionId get() = mediaPlayerUseCase.audioSessionId
//
//    suspend fun readyPlayer() {
//        viewModelScope.async {
//            mediaPlayerUseCase.readyPlayer()
//        }.await()
//    }
//
//    fun setMediaItem(pick: Pick) {
//        viewModelScope.launch {
//            if (playerState.value.id == pick.id) {
//                mediaPlayerUseCase.changeRepeatMode(false)
//            } else {
//                mediaPlayerUseCase.setMediaItem(pick)
//            }
//        }
//    }
//
//    fun setMediaItems(picks: List<Pick>) {
//        mediaPlayerUseCase.setMediaItems(picks)
//    }
//
//    private fun onPlay() {
//        mediaPlayerUseCase.play()
//    }
//
//    fun onPause() {
//        mediaPlayerUseCase.pause()
//    }
//
//    fun onStop() {
//        mediaPlayerUseCase.stop()
//    }
//
//    fun onRelease() {
//        mediaPlayerUseCase.release()
//    }
//
//    fun onPrevious() {
//        mediaPlayerUseCase.previous()
//    }
//
//    fun onNext() {
//        mediaPlayerUseCase.next()
//    }
//
//    fun onAdvanceBy() {
//        mediaPlayerUseCase.advanceBy()
//    }
//
//    fun shuffleNext(pick: Pick) {
//        if (playerState.value.isPlaying) {
//            onPause()
//        } else {
//            setMediaItem(pick)
//            onPlay()
//        }
//    }
//
//    fun togglePlayPause(song: Song) {
//        if (playerState.value.isPlaying) {
//            onPause()
//        } else {
//            onPlay()
//        }
//    }
//
//    fun onRewindBy() {
//        mediaPlayerUseCase.rewindBy()
//    }
//
//    fun onSeekingStarted() {
//        mediaPlayerUseCase.onSeekingStarted()
//    }
//
//    fun onSeekingFinished(time: Long) {
//        mediaPlayerUseCase.onSeekingFinished(time)
//    }
//
//    fun onAddToQueue(pick: Pick) {
//        mediaPlayerUseCase.addMediaItem(pick)
//    }
//}
