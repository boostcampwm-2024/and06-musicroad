package com.squirtles.musicroad.map.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.squirtles.domain.model.Creator
import com.squirtles.domain.model.LocationPoint
import com.squirtles.domain.model.Pick
import com.squirtles.domain.model.Song
import com.squirtles.musicroad.common.AlbumImage
import com.squirtles.musicroad.common.CreatedByOtherUserText
import com.squirtles.musicroad.common.CreatedBySelfText
import com.squirtles.musicroad.common.FavoriteCountText
import com.squirtles.musicroad.common.HorizontalSpacer
import com.squirtles.musicroad.common.SongInfoText
import com.squirtles.musicroad.ui.theme.Gray
import com.squirtles.musicroad.ui.theme.MusicRoadTheme

@Composable
fun InfoWindow(
    pick: Pick,
    uid: String?,
    navigateToPick: (String) -> Unit,
    calculateDistance: (Double, Double) -> String
) {
    ElevatedCard(
        onClick = { navigateToPick(pick.id) },
        modifier = Modifier
            .fillMaxWidth()
            .height(122.dp)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AlbumImage(
                imageUrl = pick.song.getImageUrlWithSize(RequestImageSize.width, RequestImageSize.height),
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                SongInfoText(
                    songInfo = "${pick.song.songName} - ${pick.song.artistName}"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pick.createdBy.uid != uid) {
                        CreatedByOtherUserText(
                            userName = pick.createdBy.userName,
                            modifier = Modifier.weight(weight = 1f, fill = false),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        CreatedBySelfText(
                            modifier = Modifier.weight(weight = 1f, fill = false),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    HorizontalSpacer(8)

                    FavoriteCountText(
                        favoriteCount = pick.favoriteCount,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pick.comment,
                        color = MaterialTheme.colorScheme.onSurface,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Text(
                text = calculateDistance(pick.location.latitude, pick.location.longitude),
                style = MaterialTheme.typography.bodyMedium.copy(Gray)
            )
        }
    }
}

@Preview("info window card")
@Preview("info window card (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun InfoWindowPreview() {
    MusicRoadTheme {
        InfoWindow(
            pick = Pick(
                id = "",
                song = Song(
                    id = "",
                    songName = "Ditto",
                    artistName = "NewJeans",
                    albumName = "Ditto",
                    imageUrl = "https://i.scdn.co/image/ab67616d0000b2733d98a0ae7c78a3a9babaf8af",
                    genreNames = listOf("KPop", "R&B", "Rap"),
                    bgColor = "#000000".toColorInt(),
                    externalUrl = "",
                    previewUrl = ""
                ),
                comment = "강남역 거리는 ditto 듣기 좋네요 ^-^!",
                createdAt = "1970.01.21",
                createdBy = Creator(uid = "", userName = "짱구"),
                favoriteCount = 100,
                location = LocationPoint(1.0, 1.0),
                musicVideoUrl = "",
            ),
            uid = "",
            navigateToPick = { },
            calculateDistance = { _, _ ->
                TODO()
            }
        )
    }
}

private val RequestImageSize = Size(400, 400)
