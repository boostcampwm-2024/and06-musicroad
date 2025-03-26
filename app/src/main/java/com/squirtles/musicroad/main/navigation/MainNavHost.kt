package com.squirtles.musicroad.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import com.squirtles.create.navigation.createNavGraph
import com.squirtles.musicroad.favorite.navigation.favoriteNavGraph
import com.squirtles.musicroad.map.MapViewModel
import com.squirtles.musicroad.map.navigation.mapNavGraph
import com.squirtles.musicroad.media.PlayerServiceViewModel
import com.squirtles.musicroad.search.navigation.searchNavGraph
import com.squirtles.musicroad.userinfo.navigation.userInfoNavGraph

@Composable
internal fun MainNavHost(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    mapViewModel: MapViewModel = hiltViewModel(),
    playerServiceViewModel: PlayerServiceViewModel = hiltViewModel(),
) {
    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
    ) {
        mapNavGraph(
            mapViewModel = mapViewModel,
            playerServiceViewModel = playerServiceViewModel,
            onFavoriteClick = navigator::navigateFavorite,
            onCenterClick = navigator::navigateSearch,
            onUserInfoClick = navigator::navigateUserInfo,
            onPickSummaryClick = navigator::navigatePickDetail,
            onBackClick = navigator::popBackStackIfNotMap,
            onDeleted = mapViewModel::resetClickedMarkerState
        )

        searchNavGraph(
            onBackClick = navigator::popBackStackIfNotMap,
            onItemClick = navigator::navigateCreate,
        )

        createNavGraph(
            onBackClick = navigator::popBackStackIfNotMap,
            onCreateClick = { pickId ->
                navigator.navigatePickDetail(pickId, true)
            },
        )

        favoriteNavGraph(
            onBackClick = navigator::popBackStackIfNotMap,
            onItemClick = navigator::navigatePickDetail
        )

        userInfoNavGraph(
            onBackClick = navigator::popBackStackIfNotMap,
            onItemClick = navigator::navigatePickDetail,
            onBackToMapClick = navigator::navigateMap,
            onFavoritePicksClick = navigator::navigateFavorite,
            onMyPicksClick = navigator::navigateMyPicks,
            onEditProfileClick = navigator::navigateEditProfile,
            onEditNotificationClick = navigator::navigateEditNotificationSetting,
        )
    }
}
