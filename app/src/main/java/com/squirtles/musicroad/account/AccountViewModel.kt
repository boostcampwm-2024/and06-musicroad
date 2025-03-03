package com.squirtles.musicroad.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.squirtles.domain.usecase.user.CreateGoogleIdUserUseCase
import com.squirtles.domain.usecase.user.DeleteAccountUseCase
import com.squirtles.domain.usecase.user.FetchUserByIdUseCase
import com.squirtles.domain.usecase.user.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val fetchUserByIdUseCase: FetchUserByIdUseCase,
    private val createGoogleIdUserUseCase: CreateGoogleIdUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) : ViewModel() {

    private val _signInSuccess = MutableSharedFlow<Boolean>()
    val signInSuccess = _signInSuccess.asSharedFlow()

    private val _signOutSuccess = MutableSharedFlow<Boolean>()
    val signOutSuccess = _signOutSuccess.asSharedFlow()

    private val _deleteAccountSuccess = MutableSharedFlow<Boolean>()
    val deleteAccountSuccess = _deleteAccountSuccess.asSharedFlow()

    fun signIn(uid: String, credential: GoogleIdTokenCredential) {
        viewModelScope.launch {
            fetchUserByIdUseCase(uid)
                .onSuccess {
                    Log.d("SignIn", "기존 계정 로그인 email : ${it.email} 로그인")
                    _signInSuccess.emit(true)
                }
                .onFailure {
                    createGoogleIdUser(uid, credential)
                }
        }
    }

    private fun createGoogleIdUser(uid: String, credential: GoogleIdTokenCredential) {
        viewModelScope.launch {
            createGoogleIdUserUseCase(
                uid = uid,
                email = credential.id,
                userName = credential.displayName,
                userProfileImage = credential.profilePictureUri.toString()
            ).onSuccess {
                Log.d("SignIn", "새로운 계정 로그인 email : ${it.email}")
                _signInSuccess.emit(true)
            }.onFailure {
                _signInSuccess.emit(false)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _signOutSuccess.emit(true)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            deleteAccountUseCase()
            _deleteAccountSuccess.emit(true)
        }
    }
}
