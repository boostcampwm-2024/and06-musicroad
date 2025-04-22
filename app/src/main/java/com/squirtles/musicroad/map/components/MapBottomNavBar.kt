package com.squirtles.musicroad.map.components

import android.content.res.Configuration
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.common.ui.theme.MusicRoadTheme
import com.squirtles.common.ui.theme.Primary
import com.squirtles.musicroad.R
import com.squirtles.musicroad.map.BottomNavigationIconSize
import com.squirtles.musicroad.map.BottomNavigationSize
import com.squirtles.musicroad.map.navigation.NavTab

@Composable
internal fun MapBottomNavBar(
    modifier: Modifier = Modifier,
    lastLocation: Location?,
    onFavoriteClick: () -> Unit,
    onCenterClick: () -> Unit,
    onUserInfoClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .size(BottomNavigationSize.WIDTH.size.dp, BottomNavigationSize.HEIGHT.size.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            // 왼쪽 버튼
            MapBottomNavigationItem(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = BottomNavigationSize.HORIZONTAL_PADDING.size.dp),
                tab = NavTab.FAVORITE,
                painter = null,
                tint = Primary,
                onClick = onFavoriteClick
            )

            // 오른쪽 버튼
            MapBottomNavigationItem(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = BottomNavigationSize.HORIZONTAL_PADDING.size.dp),
                tab = NavTab.MYPAGE,
                painter = null,
                tint = Primary,
                onClick = onUserInfoClick
            )
        }

        // 중앙 버튼
        MapBottomNavigationItem(
            modifier = Modifier
                .size(BottomNavigationIconSize.CENTER.size.dp)
                .clip(CircleShape)
                .background(
                    color = lastLocation?.let {
                        MaterialTheme.colorScheme.primary
                    } ?: Color.Gray
                ),
            tab = NavTab.SEARCH,
            painter = painterResource(R.drawable.ic_musical_note_64),
            tint = MaterialTheme.colorScheme.onPrimary,
            onClick = onCenterClick
        )
    }
}

@Composable
private fun MapBottomNavigationItem(
    modifier: Modifier = Modifier,
    tab: NavTab,
    painter: Painter?,
    tint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = if (tab.iconSize != null) Modifier.size(tab.iconSize.dp) else Modifier,
            painter = painter ?: rememberVectorPainter(tab.icon),
            contentDescription = stringResource(tab.contentDescription),
            tint = tint
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationLightPreview() {
    MusicRoadTheme {
        MapBottomNavBar(
            onFavoriteClick = {},
            lastLocation = null,
            onCenterClick = {},
            onUserInfoClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomNavigationDarkPreview() {
    MusicRoadTheme {
        MapBottomNavBar(
            onFavoriteClick = {},
            lastLocation = null,
            onCenterClick = {},
            onUserInfoClick = {}
        )
    }
}
