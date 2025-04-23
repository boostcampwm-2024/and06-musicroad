package com.squirtles.data.favorite

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.squirtles.domain.firebase.FirebaseException
import com.squirtles.core.buildconfig.LocalPropertyProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class CloudFunctionHelper {
    private val functions: FirebaseFunctions = Firebase.functions

    suspend fun updateFavoriteCount(pickId: String): Result<String> {
        return runCatching {
            val data = hashMapOf("pickId" to pickId)
            val result = functions
                .getHttpsCallable(LocalPropertyProvider.httpsCallable)
                .call(data)
                .await()

            // 성공 메시지 반환
            val message = result.getData()?.let {
                (it as? Map<*, *>)?.get("message") as? String ?: "Function executed successfully"
            } ?: "No message in response"
            message
        }.onFailure {
            Log.d("CloudFunctionHelper", "Error updating favorite count: ${it.message}")
            throw FirebaseException.CloudFunctionFailedException(exceptionMessage = it.message.toString())
        }
    }
}
