package com.example.create

sealed class SearchUiState {
    data object HotResult : SearchUiState()
    data object SearchResult : SearchUiState()
}

sealed class CreateUiState<out T> {
    data object Default : CreateUiState<Nothing>()
    data class Success<T>(val data: T) : CreateUiState<T>()
    data object Error : CreateUiState<Nothing>()
}
