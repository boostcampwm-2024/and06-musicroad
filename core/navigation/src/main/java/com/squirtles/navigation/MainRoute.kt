package com.squirtles.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : Route {
    @Serializable
    data object Search : MainRoute

    @Serializable
    data class Favorite(val userId: String) : MainRoute

    @Serializable
    data class UserInfo(val uid: String) : MainRoute
}
