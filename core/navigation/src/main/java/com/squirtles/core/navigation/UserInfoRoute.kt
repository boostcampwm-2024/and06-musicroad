package com.squirtles.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserInfoRoute : Route {
    @Serializable
    data class MyPicks(val uid: String) : UserInfoRoute

    @Serializable
    data class EditProfile(val userName: String, val userProfileImage: String?) : UserInfoRoute

    @Serializable
    data object EditNotification : UserInfoRoute

    @Serializable
    data object EditPlayer : UserInfoRoute
}

