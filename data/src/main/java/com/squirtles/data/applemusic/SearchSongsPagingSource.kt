package com.squirtles.data.applemusic

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.squirtles.data.applemusic.api.AppleMusicApi
import com.squirtles.data.applemusic.model.toSong
import com.squirtles.domain.model.Song
import retrofit2.HttpException
import java.io.IOException

class SearchSongsPagingSource(
    private val appleMusicApi: AppleMusicApi,
    private val searchText: String
) : PagingSource<Int, Song>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val pageIndex = params.key ?: 0
        return try {
            val response = appleMusicApi.searchSongs(
                storefront = DEFAULT_STOREFRONT,
                types = SEARCH_TYPES,
                term = searchText,
                limit = SEARCH_PAGE_SIZE,
                offset = (pageIndex * SEARCH_PAGE_SIZE).toString()
            )

            if (response.isSuccessful) {
                val songs = response.body()?.results?.songs?.data
                    ?.map { it.toSong() }
                    ?: emptyList()

                Log.d("SearchSongsPagingSource", "songs: $songs")

                val nextKey = if (response.body()?.results?.songs?.next == null) null else pageIndex + 1
                LoadResult.Page(
                    data = songs,
                    prevKey = if (pageIndex == 0) null else pageIndex - 1,
                    nextKey = nextKey
                )
            } else {
                val errorBody = response.errorBody()?.string()
                throw Exception(errorBody ?: "Unknown error occurred")
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        const val DEFAULT_STOREFRONT = "kr"
        const val SEARCH_TYPES = "songs"
        const val SEARCH_PAGE_SIZE = 10
    }
}
