package com.squirtles.feature.detail.navigation

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.squirtles.feature.detail.PickDetailScreen
import com.squirtles.core.musicplayer.PlayerServiceViewModel
import com.squirtles.core.navigation.MapRoute

fun NavController.navigatePickDetail(pickId: String, navOptions: NavOptions? = null) {
    navigate(MapRoute.PickDetail(pickId), navOptions)
}

fun NavGraphBuilder.detailNavGraph(
    playerServiceViewModel: PlayerServiceViewModel,
    onUserInfoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onDeleted: (Context) -> Unit,
) {
    composable<MapRoute.PickDetail> { backStackEntry ->
        val pickId = backStackEntry.toRoute<MapRoute.PickDetail>().pickId

        PickDetailScreen(
            pickId = pickId,
            playerServiceViewModel = playerServiceViewModel,
            onUserInfoClick = onUserInfoClick,
            onBackClick = onBackClick,
            onDeleted = onDeleted,
        )
    }
}
