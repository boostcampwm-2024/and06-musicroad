package com.squirtles.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface MapRoute : Route {
    @Serializable
    data class PickDetail(val pickId: String) : MapRoute
}
