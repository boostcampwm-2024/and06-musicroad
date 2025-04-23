package com.squirtles.domain.applemusic

import androidx.paging.PagingData
import com.squirtles.core.model.MusicVideo
import com.squirtles.core.model.Song
import kotlinx.coroutines.flow.Flow

interface AppleMusicRepository {
    fun searchSongs(searchText: String): Flow<PagingData<Song>>
    suspend fun searchMusicVideos(searchText: String): Result<List<MusicVideo>>
}
