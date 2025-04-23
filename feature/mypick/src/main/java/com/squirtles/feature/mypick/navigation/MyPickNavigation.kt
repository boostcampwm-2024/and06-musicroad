package com.squirtles.feature.mypick.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.squirtles.feature.mypick.MyPickScreen
import com.squirtles.core.navigation.UserInfoRoute

fun NavController.navigateMyPicks(uid: String, navOptions: NavOptions) {
    navigate(UserInfoRoute.MyPicks(uid), navOptions)
}

fun NavGraphBuilder.myPickNavGraph(
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    composable<UserInfoRoute.MyPicks> { backStackEntry ->
        val uid = backStackEntry.toRoute<UserInfoRoute.MyPicks>().uid

        MyPickScreen(
            uid = uid,
            onBackClick = onBackClick,
            onItemClick = onItemClick
        )
    }
}
