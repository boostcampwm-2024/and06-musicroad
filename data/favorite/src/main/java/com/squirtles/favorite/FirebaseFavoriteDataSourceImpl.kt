package com.squirtles.favorite

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squirtles.firebase.BaseFirebaseDataSource
import com.squirtles.firebase.FirebaseCollections
import com.squirtles.firebase.FirebaseDocumentFields
import com.squirtles.firebase.model.FirebaseFavorite
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFavoriteDataSourceImpl @Inject constructor(
    db: FirebaseFirestore,
    private val cloudFunctionHelper: CloudFunctionHelper
) : BaseFirebaseDataSource(db), FirebaseFavoriteDataSource {

    override suspend fun fetchIsFavorite(pickId: String, userId: String): Result<Boolean> {
        return runCatching {
            val favoriteDocument = queryFavoriteByPickIdAndUserId(pickId, userId).getOrThrow()
            favoriteDocument.isEmpty.not()
        }
    }

    override suspend fun createFavorite(pickId: String, userId: String): Result<String> {
        val firebaseFavorite = FirebaseFavorite(
            pickId = pickId,
            uid = userId
        )
        return runCatching {
            val addResult = addDocument(FirebaseCollections.Favorites, firebaseFavorite)
            val functionResultMessage = updateFavoriteCount(pickId).getOrThrow()
            Log.d(TAG_LOG, "Function result message: $functionResultMessage") // XXX

            addResult.getOrThrow().id
        }
    }

    override suspend fun deleteFavorite(pickId: String, userId: String): Result<String> {
        return runCatching {
            val favoriteDocRef = queryFavoriteByPickIdAndUserId(pickId, userId).getOrThrow().documents.first().reference

            deleteDocument(favoriteDocRef)

            val functionResultMessage = updateFavoriteCount(pickId).getOrThrow()

            Log.d(TAG_LOG, "Function result message: $functionResultMessage") // XXX

            favoriteDocRef.id
        }.onFailure { exception ->
            Log.w(TAG_LOG, "Error deleting favorite document", exception)
        }
    }

    private suspend fun queryFavoriteByPickIdAndUserId(pickId: String, userId: String): Result<QuerySnapshot> =
        queryDocumentsEquals(
            collection = FirebaseCollections.Favorites,
            fields = listOf(FirebaseDocumentFields.PickId, FirebaseDocumentFields.UserId),
            values = listOf(pickId, userId)
        )

    private suspend fun updateFavoriteCount(pickId: String): Result<String> {
        return cloudFunctionHelper.updateFavoriteCount(pickId)
    }

    companion object {
        private const val TAG_LOG = "FirebaseFavoriteDataSourceImpl"
    }
}
