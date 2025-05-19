package com.squirtles.feature.create.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.squirtles.core.model.Song
import com.squirtles.core.navigation.SearchRoute
import com.squirtles.core.util.serializableType
import com.squirtles.feature.create.CreatePickScreen
import kotlin.reflect.typeOf

fun NavController.navigateCreate(song: Song, navOptions: NavOptions? = null) {
    val encodedSong = song.encoded()
    navigate(SearchRoute.Create(encodedSong), navOptions)
}

fun NavGraphBuilder.createNavGraph(
    onBackClick: () -> Unit,
    onCreateClick: (String) -> Unit
) {
    composable<SearchRoute.Create>(
        typeMap = mapOf(typeOf<Song>() to serializableType<Song>())
    ) { backStackEntry ->
        val song = backStackEntry.toRoute<SearchRoute.Create>().song

        CreatePickScreen(
            song = song,
            onBackClick = onBackClick,
            onCreateClick = onCreateClick,
        )
    }
}
