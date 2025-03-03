package com.squirtles.musicroad.common.picklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.domain.model.Order
import com.squirtles.domain.model.Pick
import com.squirtles.domain.usecase.picklist.DeletePickListUseCaseInterface
import com.squirtles.domain.usecase.picklist.FetchPickListUseCaseInterface
import com.squirtles.domain.usecase.picklist.GetPickListOrderUseCaseInterface
import com.squirtles.domain.usecase.picklist.SavePickListOrderUseCaseInterface
import com.squirtles.domain.usecase.user.GetCurrentUidUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class PickListViewModel(
    val fetchPickListUseCase: FetchPickListUseCaseInterface,
    val getPickListOrderUseCase: GetPickListOrderUseCaseInterface,
    val savePickListOrderUseCase: SavePickListOrderUseCaseInterface,
    val removePickUseCase: DeletePickListUseCaseInterface,
    val getCurrentUidUseCase: GetCurrentUidUseCase
) : ViewModel() {

    private var pickList: List<Pick> = emptyList()

    private val _pickListUiState = MutableStateFlow<PickListUiState>(PickListUiState.Loading)
    val pickListUiState = _pickListUiState.asStateFlow()

    private val _selectedPicksId = MutableStateFlow<Set<String>>(emptySet())
    val selectedPicksId = _selectedPicksId.asStateFlow()

    fun fetchPickList(uid: String) {
        viewModelScope.launch {
            fetchPickListUseCase(uid)
                .onSuccess { picks ->
                    pickList = picks
                    sortPickList(getPickListOrderUseCase())
                }
                .onFailure {
                    _pickListUiState.emit(PickListUiState.Error)
                }
        }
    }

    private fun sortPickList(order: Order) {
        _pickListUiState.value = PickListUiState.Success(
            pickList = pickList.setOrderedList(order),
            order = order
        )
    }

    fun setListOrder(order: Order) {
        viewModelScope.launch {
            savePickListOrderUseCase(order)
            sortPickList(order)
        }
    }

    fun toggleSelectedPick(pickId: String) {
        val curSelectedPicksId = _selectedPicksId.value
        _selectedPicksId.value =
            if (curSelectedPicksId.contains(pickId)) curSelectedPicksId - pickId else curSelectedPicksId + pickId
    }

    fun selectAllPicks() {
        _selectedPicksId.value = pickList.map { it.id }.toSet()
    }

    fun deselectAllPicks() {
        _selectedPicksId.value = emptySet()
    }

    fun deleteSelectedPicks(uid: String) {
        viewModelScope.launch {
            _pickListUiState.value = PickListUiState.Loading

            val deleteJobList = _selectedPicksId.value.map { pickId ->
                async { removePickUseCase(pickId, uid) }
            }.awaitAll()

            deselectAllPicks()

            if (deleteJobList.all { it.isSuccess }) {
                fetchPickList(uid)
            } else {
                _pickListUiState.value = PickListUiState.Error
                Log.e("PickListViewModel", "[픽 목록] 다중 삭제 오류")
            }
        }
    }

    private fun List<Pick>.setOrderedList(order: Order): List<Pick> {
        return if (pickList.isEmpty()) this
        else when (order) {
            Order.LATEST -> this
            Order.OLDEST -> this.reversed()
            Order.FAVORITE_DESC -> this.sortedByDescending { it.favoriteCount }
        }
    }

    fun getUid() = getCurrentUidUseCase()
}
