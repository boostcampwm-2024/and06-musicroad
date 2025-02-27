package com.squirtles.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ProfileRoute : Route {
    @Serializable
    data class MyPicks(val userId: String) : ProfileRoute

    @Serializable
    data object Setting : ProfileRoute

    @Serializable
    data object Notification : ProfileRoute
}

