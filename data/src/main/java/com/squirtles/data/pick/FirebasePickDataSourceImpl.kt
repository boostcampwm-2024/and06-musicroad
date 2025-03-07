package com.squirtles.data.pick

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.squirtles.data.favorite.model.FirebaseFavorite
import com.squirtles.data.firebase.BaseFirebaseDataSource
import com.squirtles.data.firebase.FirebaseCollections
import com.squirtles.data.firebase.FirebaseDocumentFields
import com.squirtles.data.pick.model.FirebasePick
import com.squirtles.data.pick.model.toFirebasePick
import com.squirtles.data.pick.model.toPick
import com.squirtles.data.user.model.FirebaseUser
import com.squirtles.domain.model.Pick
import com.squirtles.domain.pick.FirebasePickDataSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebasePickDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : BaseFirebaseDataSource(db), FirebasePickDataSource {

    /* Fetches a pick by ID from Firestore */
    override suspend fun fetchPick(pickId: String): Result<Pick> {
        return runCatching {
            val pickSnap = fetchDocumentSnapshot(FirebaseCollections.Picks, pickId).getOrThrow()
            val firestorePick = pickSnap.toObject<FirebasePick>()?.copy(id = pickId)
            firestorePick?.toPick()!!
        }.onFailure { exception ->
            Log.e(TAG_LOG, "Failed to fetch a pick", exception)
        }
    }

    /* Fetches picks within a given radius from Firestore */
    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): Result<List<Pick>> {
        val center = GeoLocation(lat, lng)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)

        return runCatching {
            val queryResults = bounds.map { bound ->
                queryDocumentsInRange(
                    collection = FirebaseCollections.Picks,
                    field = FirebaseDocumentFields.GeoHash,
                    start = bound.startHash,
                    end = bound.endHash
                )
            }

            queryResults.flatMap { querySnapshot ->
                querySnapshot.getOrThrow().documents
                    .filter { doc ->
                        isAccurate(doc, center, radiusInM)
                    }.mapNotNull { doc ->
                        doc.toObject<FirebasePick>()?.toPick()?.copy(id = doc.id)
                    }
            }
        }.onFailure { e ->
            Log.e(TAG_LOG, "Failed to fetch picks", e)
        }
    }

    /* Creates a new pick in Firestore */
    override suspend fun createPick(pick: Pick): Result<String> {
        val firebasePick = pick.toFirebasePick()
        return runCatching {
            val pickRef = addDocument(FirebaseCollections.Picks, firebasePick).getOrThrow()
            updateCurrentUserPick(pick.createdBy.userId, pickRef.id)
            pickRef.id
        }.onFailure {
            Log.e(TAG_LOG, "Failed to create a pick", it)
        }
    }

    override suspend fun deletePick(pickId: String, userId: String): Result<String> {
        val pickDocument = fetchDocumentReference(FirebaseCollections.Picks, pickId)
        val userDocument = fetchDocumentReference(FirebaseCollections.Users, userId)

        return runCatching {
            val favoriteDocuments = fetchFavoriteDocumentRefsByPick(pickId).getOrThrow()

            db.runTransaction { transaction ->
                transaction.delete(pickDocument)
                favoriteDocuments.forEach { docRef ->
                    transaction.delete(docRef)
                }
                transaction.update(userDocument, FirebaseDocumentFields.MyPicks.name, FieldValue.arrayRemove(pickId))
            }.await()

            pickDocument.id
        }.onFailure {
            Log.e(TAG_LOG, "Failed to delete a pick", it)
        }
    }

    override suspend fun fetchMyPicks(userId: String): Result<List<Pick>> {
        return runCatching {
            val userDocument = fetchDocumentSnapshot(FirebaseCollections.Users, userId).getOrThrow()
            userDocument.toObject<FirebaseUser>()?.myPicks!!.map {
                fetchPick(it).getOrThrow()
            }.reversed()
        }
    }

    override suspend fun fetchFavoritePicks(userId: String): Result<List<Pick>> {
        return runCatching {
            val favoriteDocuments = fetchFavoritesByUserId(userId)
            favoriteDocuments.map { docSnap ->
                fetchPick(docSnap.toObject<FirebaseFavorite>()?.pickId.toString()).getOrThrow()
            }
        }
    }

    private suspend fun updateCurrentUserPick(userId: String, pickId: String): Result<Void> {
        return runCatching {
            updateDocument(
                collection = FirebaseCollections.Users,
                documentId = userId,
                field = FirebaseDocumentFields.MyPicks,
                value = FieldValue.arrayUnion(pickId)
            ).getOrThrow()
        }.onFailure { e ->
            Log.e(TAG_LOG, "Failed to update user picks", e)
        }
    }

    private suspend fun fetchFavoriteDocumentRefsByPick(pickId: String): Result<List<DocumentReference>> {
        return runCatching {
            queryDocumentsEquals(
                collection = FirebaseCollections.Favorites,
                fields = listOf(FirebaseDocumentFields.PickId),
                values = listOf(pickId),
            ).getOrThrow().documents.map { it.reference }
        }
    }

    /**
     * GeoHash의 FP 문제 - Geohash의 쿼리가 정확하지 않으며 클라이언트 측에서 거짓양성 결과를 필터링해야 합니다.
     * 이러한 추가 읽기로 인해 앱에 비용과 지연 시간이 추가됩니다.
     */
    private fun isAccurate(doc: DocumentSnapshot, center: GeoLocation, radiusInM: Double): Boolean {
        val location = doc.getGeoPoint(FirebaseDocumentFields.Location.name) ?: return false

        val docLocation = GeoLocation(location.latitude, location.longitude)
        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)

        return distanceInM <= radiusInM
    }

    private suspend fun fetchFavoritesByUserId(userId: String): List<DocumentSnapshot> {
        return queryDocumentsEquals(
            collection = FirebaseCollections.Favorites,
            fields = listOf(FirebaseDocumentFields.UserId),
            values = listOf(userId)
        ).getOrThrow().documents
    }

    private suspend fun executeQuery(query: Query): QuerySnapshot {
        return suspendCancellableCoroutine { continuation ->
            query.get()
                .addOnSuccessListener { result ->
                    continuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG_LOG, "Error fetching favorite documents", exception)
                    continuation.resumeWithException(exception)
                }
        }
    }

    companion object {
        private const val TAG_LOG = "FirebasePickDataSourceImpl"
    }
}

