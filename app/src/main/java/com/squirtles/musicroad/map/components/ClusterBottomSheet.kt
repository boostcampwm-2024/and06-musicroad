package com.squirtles.musicroad.map.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.squirtles.domain.model.LocationPoint
import com.squirtles.domain.model.Pick
import com.squirtles.domain.model.Song
import com.squirtles.musicroad.common.AlbumImage
import com.squirtles.musicroad.common.CommentText
import com.squirtles.musicroad.common.Constants.DEFAULT_PADDING
import com.squirtles.musicroad.common.Constants.REQUEST_IMAGE_SIZE_DEFAULT
import com.squirtles.musicroad.common.CountText
import com.squirtles.musicroad.common.CreatedByOtherUserText
import com.squirtles.musicroad.common.CreatedBySelfText
import com.squirtles.musicroad.common.FavoriteCountText
import com.squirtles.musicroad.common.HorizontalSpacer
import com.squirtles.musicroad.common.SongInfoText
import com.squirtles.musicroad.common.VerticalSpacer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClusterBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    clusterPickList: List<Pick>?,
    uid: String?,
    calculateDistance: (Double, Double) -> String,
    onClickItem: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        modifier = modifier,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        clusterPickList?.let { pickList ->
            CountText(
                totalCount = pickList.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = DEFAULT_PADDING)
            )
            
            VerticalSpacer(height = 8)

            LazyColumn {
                items(
                    items = pickList,
                    key = { it.id }
                ) { pick ->
                    BottomSheetItem(
                        song = pick.song,
                        pickLocation = pick.location,
                        createdUserName = pick.createdBy.userName.takeIf { pick.createdBy.uid != uid },
                        comment = pick.comment,
                        favoriteCount = pick.favoriteCount,
                        calculateDistance = calculateDistance,
                        onClickItem = {
                            scope
                                .launch { sheetState.hide() }
                                .invokeOnCompletion { onClickItem(pick.id) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomSheetItem(
    song: Song,
    pickLocation: LocationPoint,
    createdUserName: String?,
    comment: String,
    favoriteCount: Int,
    calculateDistance: (Double, Double) -> String,
    onClickItem: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickItem() }
            .padding(horizontal = DEFAULT_PADDING, vertical = DEFAULT_PADDING / 2),
        horizontalArrangement = Arrangement.spacedBy(DEFAULT_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumImage(
            imageUrl = song.getImageUrlWithSize(REQUEST_IMAGE_SIZE_DEFAULT.width, REQUEST_IMAGE_SIZE_DEFAULT.height),
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            SongInfoText(
                songInfo = "${song.songName} - ${song.artistName}"
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                createdUserName?.let { userName ->
                    CreatedByOtherUserText(
                        userName = userName,
                        modifier = Modifier.weight(weight = 1f, fill = false)
                    )
                } ?: run {
                    CreatedBySelfText(
                        modifier = Modifier.weight(weight = 1f, fill = false)
                    )
                }

                HorizontalSpacer(8)

                FavoriteCountText(
                    favoriteCount = favoriteCount
                )
            }

            CommentText(
                comment = comment
            )
        }

        Text(
            text = calculateDistance(pickLocation.latitude, pickLocation.longitude),
            modifier = Modifier.align(Alignment.Top),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
