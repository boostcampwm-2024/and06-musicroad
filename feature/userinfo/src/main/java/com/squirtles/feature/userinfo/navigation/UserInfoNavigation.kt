package com.squirtles.feature.userinfo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.squirtles.core.navigation.MainRoute
import com.squirtles.core.navigation.UserInfoRoute
import com.squirtles.feature.userinfo.screen.EditNotificationSettingScreen
import com.squirtles.feature.userinfo.screen.EditPlayerScreen
import com.squirtles.feature.userinfo.screen.EditProfileScreen
import com.squirtles.feature.userinfo.screen.UserInfoScreen

fun NavController.navigateUserInfo(uid: String, navOptions: NavOptions? = null) {
    navigate(MainRoute.UserInfo(uid), navOptions)
}

fun NavController.navigateEditProfile(userName: String, navOptions: NavOptions? = null) {
    navigate(UserInfoRoute.EditProfile(userName), navOptions)
}

fun NavController.navigateEditNotificationSetting(navOptions: NavOptions? = null) {
    navigate(UserInfoRoute.EditNotification, navOptions)
}

fun NavController.navigateToEditPlayer(navOptions: NavOptions? = null) {
    navigate(UserInfoRoute.EditPlayer, navOptions)
}

fun NavGraphBuilder.userInfoNavGraph(
    onBackClick: () -> Unit,
    onBackToMapClick: () -> Unit,
    onFavoritePicksClick: (String) -> Unit,
    onMyPicksClick: (String) -> Unit,
    onEditProfileClick: (String) -> Unit,
    onEditNotificationClick: () -> Unit,
    onEditPlayerClick : () -> Unit,
    ) {
    composable<MainRoute.UserInfo> { backStackEntry ->
        val uid = backStackEntry.toRoute<MainRoute.UserInfo>().uid

        UserInfoScreen(
            uid = uid,
            onBackClick = onBackClick,
            onBackToMapClick = onBackToMapClick,
            onFavoritePicksClick = onFavoritePicksClick,
            onMyPicksClick = onMyPicksClick,
            onEditProfileClick = onEditProfileClick,
            onEditNotificationClick = onEditNotificationClick,
            onEditPlayerClick = onEditPlayerClick,
        )
    }

    composable<UserInfoRoute.EditProfile> { backStackEntry ->
        val userName = backStackEntry.toRoute<UserInfoRoute.EditProfile>().userName
        EditProfileScreen(
            currentUserName = userName,
            onBackToMapClick = onBackToMapClick,
            onBackClick = onBackClick,
        )
    }

    composable<UserInfoRoute.EditNotification> {
        EditNotificationSettingScreen(
            onBackClick = onBackClick
        )
    }

    composable<UserInfoRoute.EditPlayer> {
        EditPlayerScreen(onBackClick = onBackClick)
    }
}
