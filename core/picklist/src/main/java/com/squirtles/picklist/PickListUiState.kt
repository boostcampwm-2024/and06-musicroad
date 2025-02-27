package com.squirtles.picklist

import com.squirtles.model.Order
import com.squirtles.model.Pick

sealed class PickListUiState {
    data object Loading : PickListUiState()
    data class Success(val pickList: List<Pick>, val order: Order) : PickListUiState()
    data object Error : PickListUiState()
}
