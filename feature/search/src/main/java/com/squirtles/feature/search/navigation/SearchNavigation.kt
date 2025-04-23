package com.squirtles.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squirtles.core.model.Song
import com.squirtles.core.navigation.MainRoute
import com.squirtles.feature.search.SearchMusicScreen

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
