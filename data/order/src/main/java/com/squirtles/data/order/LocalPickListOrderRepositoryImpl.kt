package com.squirtles.data.order

import com.squirtles.core.model.Order
import com.squirtles.domain.order.LocalPickListOrderRepository
import javax.inject.Singleton

@Singleton
class LocalPickListOrderRepositoryImpl : LocalPickListOrderRepository {
    private var _favoriteListOrder = Order.LATEST
    override val favoriteListOrder get() = _favoriteListOrder

    private var _myListOrder = Order.LATEST
    override val myListOrder get() = _myListOrder

    override suspend fun saveFavoriteListOrder(order: Order) {
        _favoriteListOrder = order
    }

    override suspend fun saveMyListOrder(order: Order) {
        _myListOrder = order
    }
}
