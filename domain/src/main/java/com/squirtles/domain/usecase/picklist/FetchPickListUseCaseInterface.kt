package com.squirtles.domain.usecase.picklist

import com.squirtles.domain.model.Pick

interface FetchPickListUseCaseInterface {
    suspend operator fun invoke(uid: String): Result<List<Pick>>
}
