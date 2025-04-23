package com.squirtles.feature.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.squirtles.core.common.ui.theme.White
import com.squirtles.detail.R

@Composable
internal fun SwipeUpIcon(
    swipeableModifier: Modifier
) {
    Box(
        modifier = swipeableModifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_swipe),
            contentDescription = stringResource(id = R.string.pick_swipe_icon_description),
            modifier = Modifier.align(Alignment.Center),
            tint = White
        )
    }
}
