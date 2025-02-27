package com.squirtles.order.usecase

import com.squirtles.order.LocalPickListOrderRepository
import com.squirtles.picklist.GetPickListOrderUseCaseInterface
import javax.inject.Inject

class GetMyPickListOrderUseCase @Inject constructor(
    private val localPickListOrderRepository: LocalPickListOrderRepository
) : GetPickListOrderUseCaseInterface {
    override suspend operator fun invoke() = localPickListOrderRepository.myListOrder
}
