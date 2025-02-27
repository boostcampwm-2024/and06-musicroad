package com.squirtles.navigation

import com.squirtles.model.Song
import kotlinx.serialization.Serializable

@Serializable
sealed interface SearchRoute : Route {
    @Serializable
    data class Create(val song: Song) : SearchRoute
}
