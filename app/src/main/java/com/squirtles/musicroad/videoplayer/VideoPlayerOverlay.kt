package com.squirtles.musicroad.videoplayer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squirtles.domain.model.Creator
import com.squirtles.domain.model.LocationPoint
import com.squirtles.domain.model.Pick
import com.squirtles.domain.model.Song
import com.squirtles.musicroad.R
import com.squirtles.musicroad.common.VerticalSpacer
import com.squirtles.musicroad.ui.theme.Black
import com.squirtles.musicroad.ui.theme.Gray
import com.squirtles.musicroad.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerOverlay(
    pick: Pick,
    onBackClick: () -> Unit,
    videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val playerState = videoPlayerViewModel.playerState.collectAsStateWithLifecycle(VideoPlayerState.Playing)
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        videoPlayerViewModel.playerState.collect {
            when (it) {
                VideoPlayerState.Playing -> alpha.animateTo(0f, animationSpec = tween(durationMillis = 300))
                else -> alpha.animateTo(1.0f, animationSpec = tween(durationMillis = 300))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { this.alpha = alpha.value }
            .background(Black.copy(alpha = alpha.value.coerceAtMost(0.5f)))
            .displayCutoutPadding()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        when (playerState.value) {
                            VideoPlayerState.Pause -> videoPlayerViewModel.setPlayState(VideoPlayerState.Playing)
                            VideoPlayerState.Playing -> videoPlayerViewModel.setPlayState(VideoPlayerState.Pause)
                            VideoPlayerState.Replay -> videoPlayerViewModel.setPlayState(VideoPlayerState.Playing)
                        }
                    }
                )
            }
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = pick.createdBy.userName + stringResource(id = R.string.pick_app_bar_title_user),
                    color = White,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
            },
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopCenter),
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    enabled = alpha.value > 0.5f
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.pick_app_bar_back_description),
                        tint = White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Box(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center)
                .background(Black.copy(0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = when (playerState.value) {
                    VideoPlayerState.Pause -> Icons.Default.PlayArrow
                    VideoPlayerState.Playing -> Icons.Default.Pause
                    VideoPlayerState.Replay -> Icons.Default.Replay
                },
                contentDescription = stringResource(id = R.string.player_play_pause_description),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                tint = White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 40.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = pick.song.songName,
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge
            )

            VerticalSpacer(8)

            Text(
                text = pick.song.artistName,
                color = Gray,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge
            )

            VerticalSpacer(24)

            if (pick.comment.isNotEmpty()) {
                Text(
                    text = pick.comment,
                    color = White,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleLarge
                )

                VerticalSpacer(24)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(id = R.string.video_quality_description),
                    modifier = Modifier
                        .background(Gray.copy(0.3f), RoundedCornerShape(8.dp))
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    color = White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Right,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Preview
@Composable
private fun VideoPlayerOverlayPreview() {
    VideoPlayerOverlay(
        pick = Pick(
            id = "",
            song = Song(
                id = "",
                songName = "Super Shy",
                artistName = "뉴진스",
                albumName = "NewJeans 'Super Shy' - Single",
                imageUrl = "https://i.scdn.co/image/ab67616d0000b2733d98a0ae7c78a3a9babaf8af",
                genreNames = listOf("KPop", "R&B", "Rap"),
                bgColor = "#8fc1e2".toColorInt(),
                externalUrl = "",
                previewUrl = ""
            ),
            comment = "강남역 거리는 Super Shy 듣기 좋네요 ^-^!",
            createdAt = "2024.11.02",
            createdBy = Creator(uid = "", userName = "짱구"),
            favoriteCount = 100,
            location = LocationPoint(1.0, 1.0),
            musicVideoUrl = "",
        ),
        onBackClick = {}
    )
}
