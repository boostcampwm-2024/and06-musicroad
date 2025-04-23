package com.squirtles.feature.map.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.ui.graphics.vector.ImageVector
import com.squirtles.feature.map.BottomNavigationIconSize
import com.squirtles.feature.map.R

internal enum class NavTab(
    @StringRes val contentDescription: Int,
    val icon: ImageVector,
    val iconSize: Int?,
) {
    FAVORITE(
        contentDescription = R.string.map_navigation_favorite_icon_description,
        icon = Icons.Default.FavoriteBorder,
        iconSize = null,
    ),

    MYPAGE(
        contentDescription = R.string.map_navigation_setting_icon_description,
        icon = Icons.Outlined.AccountCircle,
        iconSize = null,
    ),

    SEARCH(
        contentDescription = R.string.map_navigation_center_icon_description,
        icon = Icons.Outlined.MusicNote,
        iconSize = BottomNavigationIconSize.CENTER_ICON.size,
    ),
}
