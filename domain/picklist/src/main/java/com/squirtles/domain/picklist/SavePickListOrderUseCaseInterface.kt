package com.squirtles.domain.picklist

import com.squirtles.core.model.Order

interface SavePickListOrderUseCaseInterface {
    suspend operator fun invoke(order: Order)
}
