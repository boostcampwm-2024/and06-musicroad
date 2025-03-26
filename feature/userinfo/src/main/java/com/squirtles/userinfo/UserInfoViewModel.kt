package com.squirtles.userinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.model.User
import com.squirtles.user.usecase.FetchUserByIdUseCase
import com.squirtles.user.usecase.GetCurrentUidUseCase
import com.squirtles.user.usecase.UpdateUserNameUseCase
import com.squirtles.userinfo.UserInfoConstants.DEFAULT_USER
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

