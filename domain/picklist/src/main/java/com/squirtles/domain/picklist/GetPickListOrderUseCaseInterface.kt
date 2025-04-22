package com.squirtles.domain.picklist

import com.squirtles.model.Order

interface GetPickListOrderUseCaseInterface {
    suspend operator fun invoke(): Order
}
