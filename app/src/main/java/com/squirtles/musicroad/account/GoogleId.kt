package com.squirtles.musicroad.account

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.squirtles.musicroad.BuildConfig
import com.squirtles.musicroad.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleId(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .build()

    private val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    private fun signInWithGoogle(result: GetCredentialResponse, onSuccess: (String, GoogleIdTokenCredential) -> Unit) {
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

    private fun signInWithFirebase(googleIdTokenCredential: GoogleIdTokenCredential, onSuccess: (String, GoogleIdTokenCredential) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                authResult.user?.uid?.let { uid ->
                    Log.d("SignIn", "Firebase 인증 uid : $uid")
                    onSuccess(uid, googleIdTokenCredential)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SignIn", "Firebase 인증 실패", exception)
            }
    }

    fun signIn(onSuccess: (String, GoogleIdTokenCredential) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val result = credentialManager.getCredential(context, request)
                signInWithGoogle(result, onSuccess)
            }.onFailure { exception ->
                when (exception) {
                    is NoCredentialException -> Toast.makeText(context, context.getString(R.string.google_id_no_credential_exception_message), Toast.LENGTH_SHORT).show()
                }
                Log.e("SignIn", "Google SignIn Error : $exception")
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
