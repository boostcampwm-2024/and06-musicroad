package com.squirtles.musicroad.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.domain.model.User
import com.squirtles.domain.usecase.user.FetchUserByIdUseCase
import com.squirtles.domain.usecase.user.GetCurrentUidUseCase
import com.squirtles.domain.usecase.user.UpdateUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val fetchUserByIdUseCase: FetchUserByIdUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase
) : ViewModel() {

    private val _profileUser = MutableStateFlow(DEFAULT_USER)
    val profileUser = _profileUser.asStateFlow()

    val currentUid get() = getCurrentUidUseCase()

    private val _updateSuccess = MutableSharedFlow<Boolean>()
    val updateSuccess = _updateSuccess.asSharedFlow()

    fun getUserById(uid: String) {
        viewModelScope.launch {
            val user = fetchUserByIdUseCase(uid).getOrDefault(DEFAULT_USER)
            _profileUser.emit(user)
        }
    }

    fun updateUsername(newUserName: String) {
        viewModelScope.launch {
            currentUid?.let { uid ->
                val result = runCatching {
                    updateUserNameUseCase(uid, newUserName).getOrThrow()
                    fetchUserByIdUseCase(uid).getOrThrow()
                }
                _updateSuccess.emit(result.isSuccess)
            }
        }
    }
}

val DEFAULT_USER = User("", "", "", null, listOf())

