package com.squirtles.domain.applemusic.usecase

import com.squirtles.domain.applemusic.AppleMusicRepository
import javax.inject.Inject

class FetchSongsUseCase @Inject constructor(
    private val appleMusicRepository: AppleMusicRepository
) {
    operator fun invoke(searchText: String) = appleMusicRepository.searchSongs(searchText)
}
