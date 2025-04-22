package com.squirtles.map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squirtles.map.MapScreen
import com.squirtles.map.MapViewModel
import com.squirtles.musicplayer.PlayerServiceViewModel
import com.squirtles.navigation.Route

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
