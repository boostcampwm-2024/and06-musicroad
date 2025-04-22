package com.squirtles.domain.picklist

interface RemovePickUseCaseInterface {
    suspend operator fun invoke(pickId: String, userId: String): Result<String>
}
