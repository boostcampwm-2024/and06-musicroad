package com.squirtles.feature.map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squirtles.feature.map.MapScreen
import com.squirtles.feature.map.MapViewModel
import com.squirtles.core.musicplayer.PlayerServiceViewModel
import com.squirtles.core.navigation.Route

fun NavController.navigateMap(navOptions: NavOptions? = null) {
    navigate(Route.Map, navOptions)
}

fun NavGraphBuilder.mapNavGraph(
    mapViewModel: MapViewModel,
    playerServiceViewModel: PlayerServiceViewModel,
    onFavoriteClick: (String) -> Unit,
    onCenterClick: () -> Unit,
    onUserInfoClick: (String) -> Unit,
    onPickSummaryClick: (String) -> Unit,
    onLoadingDialogCloseClick: () -> Unit
) {
    composable<Route.Map> {
        MapScreen(
            mapViewModel = mapViewModel,
            playerServiceViewModel = playerServiceViewModel,
            onFavoriteClick = onFavoriteClick,
            onCenterClick = onCenterClick,
            onUserInfoClick = onUserInfoClick,
            onPickSummaryClick = onPickSummaryClick,
            onLoadingDialogCloseClick = onLoadingDialogCloseClick
        )
    }
}
