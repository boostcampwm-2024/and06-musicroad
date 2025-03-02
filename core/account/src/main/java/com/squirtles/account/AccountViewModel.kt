package com.squirtles.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.user.usecase.ClearUserUseCase
import com.squirtles.user.usecase.CreateGoogleIdUserUseCase
import com.squirtles.user.usecase.FetchUserUseCase
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val fetchUserUseCase: FetchUserUseCase,
    private val createGoogleIdUserUseCase: CreateGoogleIdUserUseCase,
    private val clearUserUseCase: ClearUserUseCase,
) : ViewModel() {

    private val _signInSuccess = MutableSharedFlow<Boolean>()
    val signInSuccess = _signInSuccess.asSharedFlow()

    private val _signOutSuccess = MutableSharedFlow<Boolean>()
    val signOutSuccess = _signOutSuccess.asSharedFlow()

    fun signIn(credential: GoogleIdTokenCredential) {
        viewModelScope.launch {
            fetchUserUseCase(credential.id)
                .onSuccess {
                    Log.d("SignIn", "기존 계정 ${it.userId} 로그인")
                    _signInSuccess.emit(true)
                }
                .onFailure {
                    createGoogleIdUser(credential)
                }
        }
    }

    private fun createGoogleIdUser(credential: GoogleIdTokenCredential) {
        viewModelScope.launch {
            createGoogleIdUserUseCase(
                userId = credential.id,
                userName = credential.displayName,
                userProfileImage = credential.profilePictureUri.toString()
            ).onSuccess {
                Log.d("SignIn", "새로운 계정 ${it.userId} 로그인")
                _signInSuccess.emit(true)
            }.onFailure {
                _signInSuccess.emit(false)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            clearUserUseCase()
                .onSuccess { _signOutSuccess.emit(true) }
                .onFailure { _signOutSuccess.emit(false) }
        }
    }
}
