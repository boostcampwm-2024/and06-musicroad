package com.squirtles.picklist

import com.squirtles.model.Order

interface GetPickListOrderUseCaseInterface {
    suspend operator fun invoke(): Order
}
