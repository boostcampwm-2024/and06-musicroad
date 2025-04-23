package com.squirtles.core.navigation

import com.squirtles.core.model.Song
import kotlinx.serialization.Serializable

@Serializable
sealed interface SearchRoute : Route {
    @Serializable
    data class Create(val song: Song) : SearchRoute
}
