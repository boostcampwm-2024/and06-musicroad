package com.squirtles.musicroad.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserInfoRoute : Route {
    @Serializable
    data class MyPicks(val uid: String) : UserInfoRoute

    @Serializable
    data class EditProfile(val userName: String) : UserInfoRoute

    @Serializable
    data object EditNotification : UserInfoRoute
}

