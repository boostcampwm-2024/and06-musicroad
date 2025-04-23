package com.squirtles.data.applemusic

import androidx.paging.PagingData
import com.squirtles.core.model.MusicVideo
import com.squirtles.core.model.Song
import kotlinx.coroutines.flow.Flow

interface AppleMusicDataSource {
    fun searchSongs(searchText: String): Flow<PagingData<Song>>
    suspend fun searchMusicVideos(searchText: String): List<MusicVideo>
}
