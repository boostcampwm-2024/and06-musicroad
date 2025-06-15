package com.squirtles.feature.permission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.squirtles.core.navigation.Route
import com.squirtles.core.navigation.UserInfoRoute
import com.squirtles.feature.permission.PermissionScreen

fun NavController.navigatePermission(navOptions: NavOptions) {
    navigate(Route.Permission, navOptions)
}

fun NavGraphBuilder.permissionNavGraph(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    composable<Route.Permission> { _ ->
        PermissionScreen(
            onBackClick = onBackClick,
            onNextClick = onNextClick
        )
    }
}
