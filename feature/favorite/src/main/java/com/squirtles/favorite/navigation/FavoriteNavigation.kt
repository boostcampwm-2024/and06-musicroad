package com.squirtles.favorite.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.squirtles.favorite.FavoriteScreen
import com.squirtles.navigation.MainRoute

fun NavController.navigateFavorite(userId: String, navOptions: NavOptions? = null) {
    navigate(MainRoute.Favorite(userId), navOptions)
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
