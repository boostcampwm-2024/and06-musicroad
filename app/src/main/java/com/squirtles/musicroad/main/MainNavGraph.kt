package com.squirtles.musicroad.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.squirtles.musicroad.MapViewModel
import com.squirtles.musicroad.favorite.FavoriteScreen
import com.squirtles.musicroad.map.MapScreen
import com.squirtles.musicroad.map.MapViewModel
import com.squirtles.musicroad.setting.SettingScreen

@Composable
fun MainNavGraph(
    mapViewModel: MapViewModel,
    navController: NavHostController,
    navigationActions: MainNavigationActions,
    mapViewModel: MapViewModel,
    modifier: Modifier = Modifier,
    startDestination: String = MainDestinations.MAIN_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(MainDestinations.MAIN_ROUTE) {
            MapScreen(
                mapViewModel = mapViewModel,
                onFavoriteClick = navigationActions.navigateToFavorite,
                onSettingClick = navigationActions.navigateToSetting,
                viewModel = mapViewModel
            )
        }

        composable(MainDestinations.FAVORITE_ROUTE) {
            FavoriteScreen()
        }

        composable(MainDestinations.SETTING_ROUTE) {
            SettingScreen()
        }
    }
}