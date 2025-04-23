package com.squirtles.feature.create

sealed class CreateUiState<out T> {
    data object Default : CreateUiState<Nothing>()
    data class Success<T>(val data: T) : CreateUiState<T>()
    data object Error : CreateUiState<Nothing>()
}
