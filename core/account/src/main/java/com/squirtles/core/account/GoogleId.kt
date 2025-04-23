package com.squirtles.core.account

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.squirtles.account.R
import com.squirtles.core.buildconfig.LocalPropertyProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GoogleId(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(LocalPropertyProvider.googleClientId)
        .setAutoSelectEnabled(true)
        .build()

    private val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    private suspend fun signInWithGoogle(
        result: GetCredentialResponse,
        onSuccess: (String, GoogleIdTokenCredential) -> Unit
    ) {
        when (val data = result.credential) {
            is CustomCredential -> {
                if (data.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(data.data)
                    Log.d("SignIn", "token : ${googleIdTokenCredential.idToken}")
                    signInWithFirebase(googleIdTokenCredential, onSuccess)
                }
            }
        }
    }

    private suspend fun signInWithFirebase(
        googleIdTokenCredential: GoogleIdTokenCredential,
        onSuccess: (String, GoogleIdTokenCredential) -> Unit
    ) = suspendCoroutine { continuation ->
        val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                authResult.user?.uid?.let { uid ->
                    Log.d("SignIn", "Firebase 인증 uid : $uid")
                    onSuccess(uid, googleIdTokenCredential)
                    continuation.resume(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SignIn", "Firebase 인증 실패", exception)
                continuation.resumeWithException(exception)
            }
    }

    fun signIn(
        onSuccess: (String, GoogleIdTokenCredential) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val result = credentialManager.getCredential(context, request)
                signInWithGoogle(result, onSuccess)
            }.onFailure { exception ->
                Log.e("SignIn", "SignIn Error : ${exception.message}")
                if (exception !is Exception) return@onFailure

                // 로그인 실패 시 콜백
                onFailure(exception)

                // 로그인 실패 시 TOAST
                val failureMessageId = when (exception) {
                    // 개발 시 GooglePlay를 지원하지 않는 에뮬레이터를 사용하는 경우, 기기에 로그인 된 구글 게정이 없는 경우 따로 메시지 표시
                    is GetCredentialProviderConfigurationException -> R.string.google_id_credential_provider_exception_message
                    is NoCredentialException -> R.string.google_id_no_credential_exception_message
                    else -> R.string.sign_in_failure_message
                }
                Toast.makeText(context, context.getString(failureMessageId), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signOut() {
        CoroutineScope(Dispatchers.Main).launch {
            credentialManager.clearCredentialState(
                request = ClearCredentialStateRequest()
            )
        }
    }
}
