package com.squirtles.musicroad.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.squirtles.model.Song
import com.squirtles.util.serializableType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
sealed interface SearchRoute : Route {
    @Serializable
    data class Create(val song: Song) : SearchRoute {
        companion object {
            val typeMap = mapOf(typeOf<Song>() to serializableType<Song>())

            fun from(savedStateHandle: SavedStateHandle) =
                savedStateHandle.toRoute<Create>(typeMap)
        }
    }
}
