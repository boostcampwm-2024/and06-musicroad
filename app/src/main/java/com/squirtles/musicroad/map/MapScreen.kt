package com.squirtles.musicroad.map

import android.content.res.Configuration
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import com.squirtles.musicroad.R
import com.squirtles.musicroad.ui.theme.MusicRoadTheme

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    onFavoriteClick: () -> Unit,
    onSettingClick: () -> Unit
) {
    Log.d("뷰모델", "map screen viewmodel: $mapViewModel")
    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    FragmentContainerView(context).apply {
                        id = View.generateViewId()

                        (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(id, MapFragment())
                            .commitAllowingStateLoss()
                    }
                }
            )

            BottomNavigation(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                onFavoriteClick = onFavoriteClick,
                onSettingClick = onSettingClick,
                onCenterClick = { mapViewModel.createMarker() }
            )
        }
    }
}

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit,
    onSettingClick: () -> Unit,
    onCenterClick: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .size(245.dp, 50.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            // 왼쪽 버튼
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onFavoriteClick() },
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.map_navigation_favorite_icon_description),
                    modifier = Modifier.padding(start = BottomNavigationHorizontalPadding),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // 오른쪽 버튼
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSettingClick() },
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = stringResource(R.string.map_navigation_setting_icon_description),
                    modifier = Modifier.padding(end = BottomNavigationHorizontalPadding),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 중앙 버튼
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary)
                .clickable { onCenterClick() /* TODO: 픽 등록 이동 */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_musical_note_64),
                contentDescription = stringResource(R.string.map_navigation_center_icon_description),
                modifier = Modifier.size(34.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationLightPreview() {
    MusicRoadTheme {
        BottomNavigation(
            onFavoriteClick = {},
            onSettingClick = {},
            onCenterClick = {}

        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomNavigationDarkPreview() {
    MusicRoadTheme {
        BottomNavigation(
            onFavoriteClick = {},
            onSettingClick = {},
            onCenterClick = {}
        )
    }
}

private val BottomNavigationHorizontalPadding = 32.dp