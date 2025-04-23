package com.squirtles.domain.order.usecase

import com.squirtles.domain.order.LocalPickListOrderRepository
import com.squirtles.domain.picklist.GetPickListOrderUseCaseInterface
import javax.inject.Inject

class GetFavoriteListOrderUseCase @Inject constructor(
    private val localPickListOrderRepository: LocalPickListOrderRepository
) : GetPickListOrderUseCaseInterface {
    override suspend operator fun invoke() = localPickListOrderRepository.favoriteListOrder
}
