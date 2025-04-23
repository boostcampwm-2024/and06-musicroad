package com.squirtles.feature.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.miller198.audiovisualizer.configs.GradientConfig
import com.miller198.audiovisualizer.configs.VisualizerConfig
import com.miller198.audiovisualizer.soundeffect.SoundEffects
import com.miller198.audiovisualizer.ui.CircleVisualizer
import com.squirtles.detail.R
import com.squirtles.core.model.Song

@Composable
internal fun CircleAlbumCover(
    audioSessionId: Int,
    song: Song,
    currentPosition: () -> Long,
    duration: () -> Long,
    audioEffectColor: Color,
    onSeekChanged: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        CircleVisualizer(
            audioSessionId = audioSessionId,
            soundEffects = SoundEffects.BAR,
            visualizerConfig = VisualizerConfig.FftCaptureConfig.Default,
            gradientConfig = GradientConfig.Enabled(
                color = audioEffectColor.mixedWhite(),
                duration = 2500
            ),
            color = audioEffectColor,
            modifier = modifier.align(Alignment.Center)
        )

        PlayCircularProgressIndicator(
            modifier = modifier
                .fillMaxSize()
                .padding(10.dp)
                .align(Alignment.Center),
            currentTime = currentPosition().toFloat(),
            duration = duration().toFloat(),
            strokeWidth = 5.dp,
            onSeekChanged = { timeMs ->
                onSeekChanged(timeMs.toLong())
            },
        )

        AsyncImage(
            model = song.getImageUrlWithSize(400, 400),
            contentDescription = song.albumName + stringResource(id = R.string.pick_album_description),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .aspectRatio(1f)
                .clip(CircleShape)
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )
    }
}

private fun Color.mixedWhite(): Color = Color(
    red = (Color.White.red + this.red) / 2,
    green = (Color.White.green + this.green) / 2,
    blue = (Color.White.blue + this.blue) / 2,
    alpha = 0.9f
)
