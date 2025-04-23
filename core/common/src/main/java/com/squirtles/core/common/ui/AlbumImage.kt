package com.squirtles.core.common.ui

import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.squirtles.common.R
import com.squirtles.core.common.ui.theme.Gray

@Composable
fun AlbumImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.map_album_image_description),
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        placeholder = ColorPainter(Gray),
        error = ColorPainter(Gray),
        contentScale = ContentScale.Crop,
    )
}

fun String.toImageUrlWithSize(size: Size): String? {
    return if (isEmpty()) null
    else replace("{w}", size.width.toString())
        .replace("{h}", size.height.toString())
}
