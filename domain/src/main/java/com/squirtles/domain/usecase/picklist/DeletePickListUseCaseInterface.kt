package com.squirtles.domain.usecase.picklist

interface DeletePickListUseCaseInterface {
    suspend operator fun invoke(pickId: String, uid: String): Result<Boolean>
}
