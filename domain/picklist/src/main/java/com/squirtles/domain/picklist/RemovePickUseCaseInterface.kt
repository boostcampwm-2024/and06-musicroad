package com.squirtles.domain.picklist

interface RemovePickUseCaseInterface {
    suspend operator fun invoke(pickId: String, uid: String): Result<String>
}
