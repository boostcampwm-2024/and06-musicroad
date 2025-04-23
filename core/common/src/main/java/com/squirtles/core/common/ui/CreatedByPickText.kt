package com.squirtles.core.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.squirtles.common.R

@Composable
fun CreatedBySelfText(
    modifier: Modifier = Modifier,
    showUnderline: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    val myPickDescription = buildAnnotatedString {
        withStyle(style = SpanStyle(textDecoration = if (showUnderline) TextDecoration.Underline else null)) {
            append(stringResource(R.string.pick_created_by_self_1))
        }
        append(" ${stringResource(R.string.pick_created_by_self_2)}")
    }

    Text(
        text = myPickDescription,
        modifier = modifier,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = style
    )
}

@Composable
fun CreatedByOtherUserText(
    userName: String,
    modifier: Modifier = Modifier,
    showUnderline: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = userName,
        modifier = modifier,
        color = color,
        textDecoration = if (showUnderline) TextDecoration.Underline else null,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = style
    )

    Text(
        text = stringResource(id = R.string.map_info_window_pick_user),
        color = color,
        style = style,
    )
}
