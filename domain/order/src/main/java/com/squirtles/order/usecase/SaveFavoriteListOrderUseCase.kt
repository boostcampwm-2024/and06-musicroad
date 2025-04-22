package com.squirtles.order.usecase

import com.squirtles.model.Order
import com.squirtles.order.LocalPickListOrderRepository
import com.squirtles.domain.picklist.SavePickListOrderUseCaseInterface
import javax.inject.Inject

class SaveFavoriteListOrderUseCase @Inject constructor(
    private val localPickListOrderRepository: LocalPickListOrderRepository
) : SavePickListOrderUseCaseInterface {
    override suspend operator fun invoke(order: Order) = localPickListOrderRepository.saveFavoriteListOrder(order)
}
