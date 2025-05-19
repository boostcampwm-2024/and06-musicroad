package com.squirtles.domain.order.usecase

import com.squirtles.core.model.Order
import com.squirtles.domain.order.LocalPickListOrderRepository
import com.squirtles.domain.picklist.SavePickListOrderUseCaseInterface
import javax.inject.Inject

class SaveMyPickListOrderUseCase @Inject constructor(
    private val localPickListOrderRepository: LocalPickListOrderRepository
) : SavePickListOrderUseCaseInterface {
    override suspend operator fun invoke(order: Order) = localPickListOrderRepository.saveMyListOrder(order)
}
