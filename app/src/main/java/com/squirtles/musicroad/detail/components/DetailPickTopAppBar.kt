package com.squirtles.musicroad.detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.squirtles.musicroad.R
import com.squirtles.musicroad.common.CreatedByOtherUserText
import com.squirtles.musicroad.common.CreatedBySelfText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPickTopAppBar(
    modifier: Modifier = Modifier,
    isCreatedBySelf: Boolean,
    isFavorite: Boolean,
    uid: String,
    userName: String,
    onDynamicBackgroundColor: Color,
    onUserInfoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onActionClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onUserInfoClick(uid) }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCreatedBySelf) {
                    CreatedBySelfText(
                        showUnderline = true,
                        color = onDynamicBackgroundColor,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                } else {
                    CreatedByOtherUserText(
                        userName = userName,
                        modifier = Modifier.weight(weight = 1f, fill = false),
                        showUnderline = true,
                        color = onDynamicBackgroundColor,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.top_app_bar_back_description),
                    tint = onDynamicBackgroundColor
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onActionClick() }
            ) {
                val painterResourceId = when {
                    isCreatedBySelf -> R.drawable.ic_delete
                    isFavorite -> R.drawable.ic_favorite_true
                    else -> R.drawable.ic_favorite_false
                }
                val stringResourceId = when {
                    isCreatedBySelf -> R.string.pick_delete_icon_description
                    isFavorite -> R.string.pick_favorite_true_icon_description
                    else -> R.string.pick_favorite_false_icon_description
                }

                Icon(
                    painter = painterResource(painterResourceId),
                    contentDescription = stringResource(stringResourceId),
                    tint = onDynamicBackgroundColor
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
