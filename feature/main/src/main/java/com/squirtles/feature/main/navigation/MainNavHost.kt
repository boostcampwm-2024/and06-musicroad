package com.squirtles.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import com.squirtles.feature.create.navigation.createNavGraph
import com.squirtles.feature.detail.navigation.detailNavGraph
import com.squirtles.feature.favorite.navigation.favoriteNavGraph
import com.squirtles.feature.map.MapViewModel
import com.squirtles.feature.map.navigation.mapNavGraph
import com.squirtles.core.musicplayer.PlayerServiceViewModel
import com.squirtles.feature.mypick.navigation.myPickNavGraph
import com.squirtles.feature.search.navigation.searchNavGraph
import com.squirtles.feature.userinfo.navigation.userInfoNavGraph

@Composable
internal fun MainNavHost(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    finishActivity: () -> Unit,
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
            onLoadingDialogCloseClick = finishActivity
        )

        searchNavGraph(
            onBackClick = navigator::popBackStackIfNotMap,
            onItemClick = navigator::navigateCreate,
        )

        detailNavGraph(
            playerServiceViewModel = playerServiceViewModel,
            onUserInfoClick = navigator::navigateUserInfo,
            onBackClick = navigator::popBackStackIfNotMap,
            onDeleted = mapViewModel::resetClickedMarkerState
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
            onBackToMapClick = navigator::navigateMap,
            onFavoritePicksClick = navigator::navigateFavorite,
            onMyPicksClick = navigator::navigateMyPicks,
            onEditProfileClick = navigator::navigateEditProfile,
            onEditNotificationClick = navigator::navigateEditNotificationSetting,
        )

        myPickNavGraph(
            onBackClick = navigator::popBackStackIfNotMap,
            onItemClick = navigator::navigatePickDetail,
        )
    }
}
