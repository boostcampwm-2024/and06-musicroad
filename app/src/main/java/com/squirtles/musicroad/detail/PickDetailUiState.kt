package com.squirtles.musicroad.detail

import com.squirtles.model.Pick

sealed class PickDetailUiState {
    data object Loading : PickDetailUiState()
    data class Success(val pick: Pick, val isFavorite: Boolean) : PickDetailUiState()
    data object Deleted : PickDetailUiState()
    data object Error : PickDetailUiState()
}
