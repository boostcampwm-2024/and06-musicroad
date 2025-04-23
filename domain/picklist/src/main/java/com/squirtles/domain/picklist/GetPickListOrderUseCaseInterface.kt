package com.squirtles.domain.picklist

import com.squirtles.core.model.Order

interface GetPickListOrderUseCaseInterface {
    suspend operator fun invoke(): Order
}
