package com.squirtles.data.applemusic

import androidx.paging.PagingData
import com.squirtles.domain.applemusic.AppleMusicException
import com.squirtles.domain.applemusic.AppleMusicRepository
import com.squirtles.core.model.MusicVideo
import com.squirtles.core.model.Song
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppleMusicRepositoryImpl @Inject constructor(
    private val appleMusicDataSource: AppleMusicDataSource
) : AppleMusicRepository {

    override fun searchSongs(searchText: String): Flow<PagingData<Song>> =
        appleMusicDataSource.searchSongs(searchText)

    override suspend fun searchMusicVideos(searchText: String): Result<List<MusicVideo>> {
        return handleResult(AppleMusicException.NotFoundException()) {
            appleMusicDataSource.searchMusicVideos(searchText).ifEmpty { null }
        }
    }

    private suspend fun <T> handleResult(
        appleMusicException: AppleMusicException,
        call: suspend () -> T?
    ): Result<T> {
        return runCatching {
            call() ?: throw appleMusicException
        }
    }
}
