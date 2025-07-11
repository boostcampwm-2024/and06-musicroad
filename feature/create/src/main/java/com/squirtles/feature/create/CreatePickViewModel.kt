package com.squirtles.feature.create

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.squirtles.core.model.Creator
import com.squirtles.core.model.LocationPoint
import com.squirtles.core.model.Pick
import com.squirtles.core.model.Song
import com.squirtles.core.navigation.SearchRoute
import com.squirtles.core.util.serializableType
import com.squirtles.core.util.throttleFirst
import com.squirtles.domain.applemusic.usecase.FetchMusicVideoUseCase
import com.squirtles.domain.location.usecase.GetLastLocationUseCase
import com.squirtles.domain.pick.usecase.CreatePickUseCase
import com.squirtles.domain.user.usecase.FetchUserByIdUseCase
import com.squirtles.domain.user.usecase.GetCurrentUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class CreatePickViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getLastLocationUseCase: GetLastLocationUseCase,
    private val fetchMusicVideoUseCase: FetchMusicVideoUseCase,
    private val createPickUseCase: CreatePickUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val fetchUserByIdUseCase: FetchUserByIdUseCase
) : ViewModel() {

    private val typeMap = mapOf(typeOf<Song>() to serializableType<Song>())
    private val song = savedStateHandle.toRoute<SearchRoute.Create>(typeMap).song

    private val _createPickUiState = MutableStateFlow<CreateUiState<String>>(CreateUiState.Default)
    val createPickUiState = _createPickUiState.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment get() = _comment

    private var lastLocation: LocationPoint? = null
    private val createPickClick = MutableSharedFlow<Unit>()

    init {
        // 데이터소스의 위치값을 계속 collect하며 curLocation 변수에 저장
        viewModelScope.launch {
            getLastLocationUseCase().collect { location ->
                lastLocation = location
            }
        }

        // 등록 버튼 클릭 후 3초 이내의 클릭은 무시하고 픽 생성하기
        viewModelScope.launch {
            createPickClick
                .throttleFirst(3000)
                .collect {
                    createPick(song)
                }
        }
    }

    fun onCommentChange(text: String) {
        _comment.value = text
    }

    fun resetComment() {
        _comment.value = ""
    }

    fun onCreatePickClick() {
        viewModelScope.launch {
            createPickClick.emit(Unit)
        }
    }

    private fun createPick(song: Song) {
        viewModelScope.launch {
            if (lastLocation == null) {
                /* TODO: DEFAULT 인 경우 -> LocalDataSource 위치 데이터 못 불러옴 */
                return@launch
            }

            val musicVideo = fetchMusicVideoUseCase(song)
            val currentUid = getCurrentUidUseCase()
            /* 등록 결과 - pick ID 담긴 Result */
            if (currentUid != null) {
                fetchUserByIdUseCase(currentUid)
                    .onSuccess { user ->
                        val createResult = createPickUseCase(
                            Pick(
                                id = "",
                                song = song,
                                comment = _comment.value,
                                createdAt = "",
                                createdBy = Creator(
                                    uid = user.uid,
                                    userName = user.userName
                                ),
                                location = LocationPoint(lastLocation!!.latitude, lastLocation!!.longitude),
                                musicVideoUrl = musicVideo?.previewUrl ?: "",
                                musicVideoThumbnailUrl = musicVideo?.thumbnailUrl ?: ""
                            )
                        )

                        createResult.onSuccess { pickId ->
                            _createPickUiState.emit(CreateUiState.Success(pickId))
                        }.onFailure {
                            /* TODO: Firestore 등록 실패처리 */
                            _createPickUiState.emit(CreateUiState.Error)
                            Log.d("CreatePickViewModel", createResult.exceptionOrNull()?.message.toString())
                        }
                    }
            }
        }
    }
}
