package com.example.create

import android.location.Location
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.applemusic.usecase.FetchMusicVideoUseCase
import com.example.location.usecase.GetLastLocationUseCase
import com.example.model.Creator
import com.example.model.LocationPoint
import com.example.model.Pick
import com.example.model.Song
import com.example.navigation.SearchRoute
import com.example.pick.usecase.CreatePickUseCase
import com.example.user.usecase.GetCurrentUserUseCase
import com.example.util.serializableType
import com.example.util.throttleFirst
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@OptIn(FlowPreview::class)
@HiltViewModel
class CreatePickViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getLastLocationUseCase: GetLastLocationUseCase,
    private val fetchMusicVideoUseCase: FetchMusicVideoUseCase,
    private val createPickUseCase: CreatePickUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val song =
        savedStateHandle.toRoute<SearchRoute.Create>(mapOf(typeOf<Song>() to serializableType<Song>())).song

    private val _createPickUiState = MutableStateFlow<CreateUiState<String>>(CreateUiState.Default)
    val createPickUiState = _createPickUiState.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment get() = _comment

    private var lastLocation: Location? = null
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

            /* 등록 결과 - pick ID 담긴 Result */
            getCurrentUserUseCase()?.let { user ->
                val createResult = createPickUseCase(
                    Pick(
                        id = "",
                        song = song,
                        comment = _comment.value,
                        createdAt = "",
                        createdBy = Creator(
                            userId = user.userId,
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
