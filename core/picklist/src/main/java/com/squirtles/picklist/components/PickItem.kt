package com.squirtles.picklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.squirtles.common.ui.AlbumImage
import com.squirtles.common.ui.CommentText
import com.squirtles.common.ui.Constants
import com.squirtles.common.ui.CreatedByOtherUserText
import com.squirtles.common.ui.FavoriteCountText
import com.squirtles.common.ui.HorizontalSpacer
import com.squirtles.common.ui.SongInfoText
import com.squirtles.common.ui.theme.Gray
import com.squirtles.common.ui.theme.Primary
import com.squirtles.common.ui.theme.White
import com.squirtles.model.Song
import com.squirtles.picklist.R

@Composable
internal fun PickItem(
    isEditMode: Boolean,
    isSelected: Boolean,
    song: Song,
    createdByOthers: Boolean,
    createUserName: String,
    favoriteCount: Int,
    comment: String,
    createdAt: String,
    onItemClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = if (isSelected) White.copy(alpha = 0.2F) else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = White),
            ) { onItemClick() }
            .padding(
                horizontal = Constants.DEFAULT_PADDING,
                vertical = Constants.DEFAULT_PADDING / 2
            ),
        horizontalArrangement = Arrangement.spacedBy(Constants.DEFAULT_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumImage(
            imageUrl = song.getImageUrlWithSize(
                Constants.REQUEST_IMAGE_SIZE_DEFAULT.width,
                Constants.REQUEST_IMAGE_SIZE_DEFAULT.height
            ),
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            SongInfoText(
                songInfo = "${song.songName} - ${song.artistName}",
                color = White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (createdByOthers) {
                    CreatedByOtherUserText(
                        userName = createUserName,
                        modifier = Modifier.weight(weight = 1f, fill = false),
                        color = Gray
                    )
                } else {
                    Text(
                        text = createdAt,
                        modifier = Modifier.weight(weight = 1f, fill = false),
                        color = Gray,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                HorizontalSpacer(8)

                FavoriteCountText(
                    favoriteCount = favoriteCount,
                    iconTint = Gray,
                    color = Gray
                )
            }

            CommentText(
                comment = comment,
                color = Gray
            )
        }

        if (isEditMode) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = stringResource(R.string.outlined_check_circle_icon_description),
                tint = if (isSelected) Primary else Gray
            )
        }
    }
}
