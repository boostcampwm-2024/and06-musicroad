package com.squirtles.musicroad.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : Route {
    @Serializable
    data object Search : MainRoute

    @Serializable
    data class Favorite(val uid: String) : MainRoute

    @Serializable
    data class UserInfo(val uid: String) : MainRoute
}
