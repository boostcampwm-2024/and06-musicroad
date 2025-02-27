package com.squirtles.order

import com.squirtles.model.Order

interface LocalPickListOrderRepository {
    val favoriteListOrder: Order // 픽 보관함 정렬 순서
    val myListOrder: Order // 등록한 픽 정렬 순서

    suspend fun saveFavoriteListOrder(order: Order)
    suspend fun saveMyListOrder(order: Order)
}
