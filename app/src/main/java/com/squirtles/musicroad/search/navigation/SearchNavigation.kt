package com.squirtles.musicroad.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squirtles.model.Song
import com.squirtles.musicroad.navigation.MainRoute
import com.squirtles.musicroad.search.SearchMusicScreen

fun NavController.navigateSearch(navOptions: NavOptions? = null) {
    navigate(MainRoute.Search, navOptions)
}

fun NavGraphBuilder.searchNavGraph(
    onBackClick: () -> Unit,
    onItemClick: (Song) -> Unit,
) {
    composable<MainRoute.Search> {
        SearchMusicScreen(
            onBackClick = onBackClick,
            onItemClick = onItemClick, // Create 이동
        )
    }
}
