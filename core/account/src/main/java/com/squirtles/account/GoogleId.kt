package com.squirtles.account

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

    private fun handleSignIn(result: GetCredentialResponse, onSuccess: (GoogleIdTokenCredential) -> Unit) {
        when (val data = result.credential) {
            is CustomCredential -> {
                if (data.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(data.data)
                    Log.d("GoogleId", "data.type : ${googleIdTokenCredential.id}")
                    Log.d("GoogleId", "data.type : ${googleIdTokenCredential.displayName}")
                    Log.d("GoogleId", "data.type : ${googleIdTokenCredential.profilePictureUri.toString()}")
                    onSuccess(googleIdTokenCredential)
                }
            }
        }
    }

    fun signIn(onSuccess: (GoogleIdTokenCredential) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val result = credentialManager.getCredential(context, request)
                handleSignIn(result, onSuccess)
            }.onFailure { exception ->
                when (exception) {
                    is NoCredentialException -> Toast.makeText(
                        context,
                        context.getString(R.string.google_id_no_credential_exception_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Log.e("GoogleId", "Google SignIn Error : $exception")
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
