package com.squirtles.musicroad.map.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squirtles.musicplayer.PlayerServiceViewModel
import com.squirtles.musicroad.map.MapScreen
import com.squirtles.musicroad.map.MapViewModel
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
) {
    composable<Route.Map> {
        MapScreen(
            mapViewModel = mapViewModel,
            playerServiceViewModel = playerServiceViewModel,
            onFavoriteClick = onFavoriteClick,
            onCenterClick = onCenterClick,
            onUserInfoClick = onUserInfoClick,
            onPickSummaryClick = onPickSummaryClick,
        )
    }
}
