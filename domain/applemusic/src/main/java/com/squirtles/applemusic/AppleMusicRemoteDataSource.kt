package com.squirtles.applemusic

import androidx.paging.PagingData
import com.squirtles.model.MusicVideo
import com.squirtles.model.Song
import kotlinx.coroutines.flow.Flow

interface AppleMusicRemoteDataSource {
    fun searchSongs(searchText: String): Flow<PagingData<Song>>
    suspend fun searchMusicVideos(searchText: String): List<MusicVideo>
}
