package com.squirtles.feature.userinfo.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.squirtles.core.common.ui.theme.White

data class MenuItem(
    val imageVector: ImageVector,
    val contentDescription: String,
    val iconColor: Color = White,
    val menuTitle: String,
    val onMenuClick: () -> Unit
)
