package com.squirtles.search

sealed class SearchUiState {
    data object HotResult : SearchUiState()
    data object SearchResult : SearchUiState()
}
