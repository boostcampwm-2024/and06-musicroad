package com.squirtles.data.applemusic.model

import androidx.core.graphics.toColorInt
import com.squirtles.core.model.MusicVideo
import com.squirtles.core.model.Song
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal fun Data.toSong(): Song = Song(
    id = id,
    songName = this.attributes.songName,
    artistName = this.attributes.artistName,
    albumName = this.attributes.albumName.toString(),
    imageUrl = this.attributes.artwork.url,
    genreNames = this.attributes.genreNames,
    bgColor = "#${this.attributes.artwork.bgColor}".toColorInt(),
    externalUrl = this.attributes.externalUrl,
    previewUrl = this.attributes.previews[0].url.toString(),
)

internal fun Data.toMusicVideo(): MusicVideo = MusicVideo(
    id = id,
    songName = this.attributes.songName,
    artistName = this.attributes.artistName,
    albumName = this.attributes.albumName.toString(),
    releaseDate = this.attributes.releaseDate?.toLocalDate() ?: DEFAULT_DATE,
    previewUrl = this.attributes.previews[0].url.toString(),
    thumbnailUrl = this.attributes.previews[0].artwork?.url.toString()
)

private fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

private val DEFAULT_DATE = LocalDate.of(2000, 1, 1)
