package com.squirtles.create.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.squirtles.model.Song
import com.squirtles.musicroad.create.CreatePickScreen
import com.squirtles.musicroad.navigation.SearchRoute
import com.squirtles.util.serializableType
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.typeOf

fun NavController.navigateCreate(song: Song, navOptions: NavOptions? = null) {
    val encodedSong = song.copy(
        previewUrl = URLEncoder.encode(song.previewUrl, StandardCharsets.UTF_8.toString()),
        externalUrl = URLEncoder.encode(song.externalUrl, StandardCharsets.UTF_8.toString()),
        genreNames = song.genreNames.map { URLEncoder.encode(it, StandardCharsets.UTF_8.toString()) },
        imageUrl = URLEncoder.encode(song.imageUrl, StandardCharsets.UTF_8.toString())
    )
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
