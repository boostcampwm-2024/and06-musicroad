package com.squirtles.musicroad.favorite.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.squirtles.musicroad.favorite.FavoriteScreen
import com.squirtles.musicroad.navigation.MainRoute

fun NavController.navigateFavorite(uid: String, navOptions: NavOptions? = null) {
    navigate(MainRoute.Favorite(uid), navOptions)
}

fun NavGraphBuilder.favoriteNavGraph(
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    composable<MainRoute.Favorite> { backStackEntry ->
        val uid = backStackEntry.toRoute<MainRoute.Favorite>().uid

        FavoriteScreen(
            uid = uid,
            onBackClick = onBackClick,
            onItemClick = onItemClick,
        )
    }
}
