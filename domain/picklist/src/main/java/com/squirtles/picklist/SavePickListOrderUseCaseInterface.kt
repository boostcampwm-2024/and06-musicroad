package com.squirtles.picklist

import com.squirtles.model.Order

interface SavePickListOrderUseCaseInterface {
    suspend operator fun invoke(order: Order)
}
