package com.squirtles.picklist

import com.squirtles.model.Pick

interface FetchPickListUseCaseInterface {
    suspend operator fun invoke(userId: String): Result<List<Pick>>
}
