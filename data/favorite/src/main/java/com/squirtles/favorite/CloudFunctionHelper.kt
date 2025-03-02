package com.squirtles.favorite

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.squirtles.localproperties.LocalPropertyProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class CloudFunctionHelper {
    private val functions: FirebaseFunctions = Firebase.functions

    suspend fun updateFavoriteCount(pickId: String): Result<String> {
        return try {
            val data = hashMapOf("pickId" to pickId)
            val result = functions
                .getHttpsCallable(LocalPropertyProvider.httpsCallable)
                .call(data)
                .await()

            // 성공 메시지 반환
            val message = result.getData()?.let {
                (it as? Map<*, *>)?.get("message") as? String ?: "Function executed successfully"
            } ?: "No message in response"
            Result.success(message)
        } catch (e: Exception) {
            // 에러 처리
            Result.failure(e)
        }
    }
}
