package com.squirtles.core.common.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.squirtles.common.R
import com.squirtles.core.common.ui.theme.Primary

@Composable
fun SongInfoText(
    songInfo: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = songInfo,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
fun FavoriteCountText(
    favoriteCount: Int,
    iconTint: Color = Primary,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_favorite),
        contentDescription = stringResource(R.string.map_info_window_favorite_count_icon_description),
        tint = iconTint
    )

    Text(
        text = " $favoriteCount",
        color = color,
        style = style,
    )
}

@Composable
fun CommentText(
    comment: String,
    color: Color = MaterialTheme.colorScheme.onSecondary,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = 1,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = comment,
        color = color,
        overflow = overflow,
        maxLines = maxLines,
        style = style,
    )
}

@Composable
fun CountText(
    totalCount: Int,
    modifier: Modifier = Modifier,
    countLabel: String = stringResource(R.string.total_count_text),
    defaultColor: Color = MaterialTheme.colorScheme.onSurface,
    pointColor: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = defaultColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("$countLabel ")
            }
            withStyle(
                SpanStyle(
                    color = pointColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("$totalCount")
            }
        },
        modifier = modifier,
        style = style
    )
}
