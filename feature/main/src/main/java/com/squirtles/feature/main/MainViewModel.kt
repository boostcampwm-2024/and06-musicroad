package com.squirtles.feature.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.domain.firebase.FirebaseException
import com.squirtles.domain.user.usecase.FetchUserByIdUseCase
import com.squirtles.domain.user.usecase.GetCurrentUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchUserByIdUseCase: FetchUserByIdUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingState = _loadingState.asStateFlow()

    private var _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted = _isPermissionGranted.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentUidUseCase().let { uid ->
                Log.d("AutoLogin", "현재 uid : $uid")
                if (uid == null) { // 비로그인 상태
                    _loadingState.emit(LoadingState.Success(null))
                } else {
                    fetchUser(uid)
                }
            }
        }
    }

    fun setPermissionGranted(isGranted: Boolean) {
        viewModelScope.launch {
            _isPermissionGranted.emit(isGranted)
        }
    }

    private suspend fun fetchUser(uid: String) {
        fetchUserByIdUseCase(uid)
            .onSuccess {
                _loadingState.emit(LoadingState.Success(it.uid))
            }
            .onFailure { exception ->
                when (exception) {
                    is FirebaseException.FetchDocumentFailedException -> {
                        _loadingState.emit(LoadingState.UserNotFoundError(exception.message))
                    }

                    else -> {
                        _loadingState.emit(LoadingState.NetworkError(exception.message.toString()))
                    }
                }
            }
    }
}
