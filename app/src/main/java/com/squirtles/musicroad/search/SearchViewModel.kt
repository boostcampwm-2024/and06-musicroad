package com.squirtles.musicroad.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.squirtles.applemusic.usecase.FetchSongsUseCase
import com.squirtles.model.Song
import com.squirtles.musicroad.create.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val fetchSongsUseCase: FetchSongsUseCase,
) : ViewModel() {

    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.HotResult)
    val searchUiState = _searchUiState.asStateFlow()

    private val _searchResult = MutableStateFlow<PagingData<Song>>(PagingData.empty())
    val searchResult = _searchResult.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            _searchText
                .debounce(300)
                .collect { searchKeyword ->
                    searchJob?.cancel()
                    if (searchKeyword.isNotBlank()) {
                        searchJob = launch { searchSongs(searchKeyword) }
                    } else {
                        _searchUiState.emit(SearchUiState.HotResult)
                    }
                }
        }
    }

    private suspend fun searchSongs(searchKeyword: String) {
        fetchSongsUseCase(searchKeyword)
            .cachedIn(viewModelScope)
            .collectLatest {
                _searchResult.emit(it)
                _searchUiState.emit(SearchUiState.SearchResult)
            }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
}
