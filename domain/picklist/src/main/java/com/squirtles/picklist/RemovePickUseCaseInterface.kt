package com.squirtles.picklist

interface RemovePickUseCaseInterface {
    suspend operator fun invoke(pickId: String, userId: String): Result<String>
}
