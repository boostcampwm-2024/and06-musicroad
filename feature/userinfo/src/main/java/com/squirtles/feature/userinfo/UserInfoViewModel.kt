package com.squirtles.feature.userinfo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.domain.user.usecase.DeleteUserProfileImageUseCase
import com.squirtles.domain.user.usecase.FetchUserByIdUseCase
import com.squirtles.domain.user.usecase.GetCurrentUidUseCase
import com.squirtles.domain.user.usecase.UpdateUserNameUseCase
import com.squirtles.domain.user.usecase.UpdateUserProfileImageUseCase
import com.squirtles.feature.userinfo.UserInfoConstants.DEFAULT_USER
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserNameState {
    data object Unchanged : UserNameState()
    data class New(val userName: String) : UserNameState()
}

sealed class ProfileImageState {
    data object Unchanged : ProfileImageState()
    data object Remove : ProfileImageState()
    data class New(val userProfileImage: Uri) : ProfileImageState()
}

data class UpdateState(
    val nameSuccess: Boolean,
    val imageSuccess: Boolean
)

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCurrentUidUseCase: GetCurrentUidUseCase,
    private val fetchUserByIdUseCase: FetchUserByIdUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val updateUserProfileImageUseCase: UpdateUserProfileImageUseCase,
    private val deleteUserProfileImageUseCase: DeleteUserProfileImageUseCase
) : ViewModel() {

    private val _profileUser = MutableStateFlow(DEFAULT_USER)
    val profileUser = _profileUser.asStateFlow()

    val currentUid get() = getCurrentUidUseCase()

    private val _updateState = MutableSharedFlow<UpdateState>()
    val updateState = _updateState.asSharedFlow()

    fun getUserById(uid: String) {
        viewModelScope.launch {
            val user = fetchUserByIdUseCase(uid).getOrDefault(DEFAULT_USER)
            _profileUser.emit(user)
        }
    }

    fun updateProfile(userNameState: UserNameState, profileImageState: ProfileImageState) {
        viewModelScope.launch {
            val nameResult = updateUsername(userNameState)
            val imageResult = updateUserProfileImage(profileImageState)

            if (!nameResult) Log.e("UserInfoViewModel", "닉네임 변경 실패")
            if (!imageResult) Log.e("UserInfoViewModel", "프로필 사진 변경 실패")

            if (nameResult || imageResult) {
                currentUid?.let { uid ->
                    runCatching {
                        fetchUserByIdUseCase(uid).getOrThrow()
                    }
                }
            }

            _updateState.emit(
                UpdateState(
                    nameSuccess = nameResult,
                    imageSuccess = imageResult
                )
            )
        }
    }

    private suspend fun updateUsername(userNameState: UserNameState): Boolean {
        val userName = when (userNameState) {
            is UserNameState.Unchanged -> {
                return true
            }

            is UserNameState.New -> {
                userNameState.userName
            }
        }

        return currentUid?.let { uid ->
            runCatching {
                updateUserNameUseCase(uid, userName).getOrThrow()
            }.isSuccess
        } ?: false
    }

    private suspend fun updateUserProfileImage(profileImageState: ProfileImageState): Boolean {
        return when (profileImageState) {
            is ProfileImageState.Unchanged -> {
                true
            }

            is ProfileImageState.Remove -> {
                currentUid?.let { uid ->
                    runCatching {
                        deleteUserProfileImageUseCase(uid)
                    }.isSuccess
                } ?: false
            }

            is ProfileImageState.New -> {
                val newImageData: ByteArray = profileImageState.userProfileImage.toByteArray(context) ?: return false
                currentUid?.let { uid ->
                    runCatching {
                        updateUserProfileImageUseCase(uid, newImageData)
                    }.isSuccess
                } ?: false
            }
        }
    }

    private fun Uri.toByteArray(context: Context): ByteArray? {
        return try {
            context.contentResolver.openInputStream(this)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

