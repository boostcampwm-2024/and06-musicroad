package com.squirtles.musicroad.detail

import androidx.core.graphics.toColorInt
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.domain.model.Creator
import com.squirtles.domain.model.LocationPoint
import com.squirtles.domain.model.Pick
import com.squirtles.domain.model.Song
import com.squirtles.domain.usecase.favorite.CreateFavoriteUseCase
import com.squirtles.domain.usecase.favorite.DeleteFavoriteUseCase
import com.squirtles.domain.usecase.mypick.DeletePickUseCase
import com.squirtles.domain.usecase.pick.FetchIsFavoriteUseCase
import com.squirtles.domain.usecase.pick.FetchPickUseCase
import com.squirtles.domain.usecase.user.GetCurrentUidUseCase
import com.squirtles.musicroad.utils.throttleFirst
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fetchPickUseCase: FetchPickUseCase,
    private val fetchIsFavoriteUseCase: FetchIsFavoriteUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val deletePickUseCase: DeletePickUseCase,
    private val createFavoriteUseCase: CreateFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
) : ViewModel() {

    private val _pickDetailUiState = MutableStateFlow<PickDetailUiState>(PickDetailUiState.Loading)
    val pickDetailUiState = _pickDetailUiState.asStateFlow()

    private var _currentTab = DETAIL_PICK_TAB
    val currentTab get() = _currentTab

    private val _favoriteAction = MutableSharedFlow<FavoriteAction>()
    val favoriteAction = _favoriteAction.asSharedFlow()

    private val actionClick = MutableSharedFlow<Pair<String, Boolean>>()

    init {
        viewModelScope.launch {
            actionClick
                .throttleFirst(1000)
                .collect { (pickId, isAdding) ->
                    getUid()?.let { uid ->
                        if (isAdding) {
                            addToFavoritePicks(pickId, uid)
                        } else {
                            deleteFromFavoritePicks(pickId, uid)
                        }
                    }
                }
        }
    }

    fun getUid() = getCurrentUidUseCase()

    fun fetchPick(pickId: String) {
        viewModelScope.launch {
            _pickDetailUiState.emit(PickDetailUiState.Loading)

            val fetchPick = async { fetchPickUseCase(pickId) }

            val fetchIsFavoriteResult: Result<Boolean> = getUid()?.let {
                async { fetchIsFavoriteUseCase(pickId, it) }.await()
            } ?: Result.success(false) // 비회원일시 항상 false
            val fetchPickResult = fetchPick.await()

            when {
                fetchPickResult.isSuccess && fetchIsFavoriteResult.isSuccess -> {
                    _pickDetailUiState.emit(
                        PickDetailUiState.Success(
                            pick = fetchPickResult.getOrDefault(DEFAULT_PICK),
                            isFavorite = fetchIsFavoriteResult.getOrDefault(false)
                        )
                    )
                }

                else -> {
                    _pickDetailUiState.emit(PickDetailUiState.Error)
                }
            }
        }
    }

    fun toggleFavoritePick(pickId: String, isAdding: Boolean) {
        viewModelScope.launch {
            actionClick.emit(pickId to isAdding)
        }
    }

    fun deletePick(pickId: String) {
        viewModelScope.launch {
            getUid()?.let { uid ->
                _pickDetailUiState.emit(PickDetailUiState.Loading)
                deletePickUseCase(pickId, uid)
                    .onSuccess {
                        _pickDetailUiState.emit(PickDetailUiState.Deleted)
                    }
                    .onFailure {
                        _pickDetailUiState.emit(PickDetailUiState.Error)
                    }
            }
        }
    }

    private fun addToFavoritePicks(pickId: String, uid: String) {
        viewModelScope.launch {
            createFavoriteUseCase(pickId, uid)
                .onSuccess {
                    _favoriteAction.emit(FavoriteAction.ADDED)
                    val currentUiState = _pickDetailUiState.value as? PickDetailUiState.Success
                    currentUiState?.let { successState ->
                        _pickDetailUiState.emit(successState.copy(isFavorite = true))
                    }
                }
                .onFailure {
                    _pickDetailUiState.emit(PickDetailUiState.Error)
                }
        }
    }

    private fun deleteFromFavoritePicks(pickId: String, uid: String) {
        viewModelScope.launch {
            deleteFavoriteUseCase(pickId, uid)
                .onSuccess {
                    _favoriteAction.emit(FavoriteAction.DELETED)
                    val currentUiState = _pickDetailUiState.value as? PickDetailUiState.Success
                    currentUiState?.let { successState ->
                        _pickDetailUiState.emit(successState.copy(isFavorite = false))
                    }
                }
                .onFailure {
                    _pickDetailUiState.emit(PickDetailUiState.Error)
                }
        }
    }

    fun setCurrentTab(index: Int) {
        _currentTab = index
    }

    companion object {
        val DEFAULT_PICK =
            Pick(
                id = "",
                song = Song(
                    id = "",
                    songName = "",
                    artistName = "",
                    albumName = "",
                    imageUrl = "",
                    genreNames = listOf(),
                    bgColor = "#000000".toColorInt(),
                    externalUrl = "",
                    previewUrl = ""
                ),
                comment = "",
                createdAt = "",
                createdBy = Creator(uid = "", userName = "짱구"),
                favoriteCount = 0,
                location = LocationPoint(1.0, 1.0),
                musicVideoUrl = "",
            )
    }
}
