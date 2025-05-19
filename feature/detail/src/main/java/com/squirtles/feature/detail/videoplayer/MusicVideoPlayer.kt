package com.squirtles.feature.detail.videoplayer

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import dagger.hilt.android.UnstableApi
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun MusicVideoPlayer(
    musicVideoUrl: String,
    videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val textureView = remember { TextureView(context) }
    var currentSurfaceTexture by remember { mutableStateOf<SurfaceTexture?>(null) }
    val videoSize by videoPlayerViewModel.videoSize.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            videoPlayerViewModel.pause()
            videoPlayerViewModel.setLastPosition()
            textureView.surfaceTexture?.release()
            textureView.surfaceTextureListener = null
        }
    }

    LaunchedEffect(currentSurfaceTexture) {
        if (currentSurfaceTexture != null) {
            val surface = Surface(currentSurfaceTexture)
            videoPlayerViewModel.setSurface(surface)
        }
    }

    AndroidView(
        factory = {
            videoPlayerViewModel.initializePlayer(context, musicVideoUrl)
            textureView.apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                        currentSurfaceTexture = surfaceTexture
                        coroutineScope.launch {
                            videoPlayerViewModel.videoSize
                                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                                .collect {
                                    setVideoSize(textureView, it.width, it.height)
                                }
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {

                    }

                    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                        videoPlayerViewModel.setSurface(null)
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
                        // TODO
                    }
                }
            }
        }
    )
}

private fun setVideoSize(textureView: TextureView, videoWidth: Int, videoHeight: Int) {
    // 비율 조정
    val viewWidth = textureView.width.toFloat()
    val viewHeight = textureView.height.toFloat()
    val videoAspect = videoWidth.toFloat() / videoHeight
    val viewAspect = viewWidth / viewHeight
    val scaleX: Float
    val scaleY: Float

    if (videoAspect > viewAspect) {
        scaleX = videoAspect / viewAspect
        scaleY = 1f
    } else {
        scaleX = 1f
        scaleY = viewAspect / videoAspect
    }

    // 중앙 정렬
    val matrix = Matrix()
    matrix.setScale(scaleX, scaleY, viewWidth / 2, viewHeight / 2)

    textureView.setTransform(matrix)
}
