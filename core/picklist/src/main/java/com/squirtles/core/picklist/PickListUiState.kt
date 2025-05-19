package com.squirtles.core.picklist

import com.squirtles.core.model.Order
import com.squirtles.core.model.Pick

sealed class PickListUiState {
    data object Loading : PickListUiState()
    data class Success(val pickList: List<Pick>, val order: Order) : PickListUiState()
    data object Error : PickListUiState()
}
