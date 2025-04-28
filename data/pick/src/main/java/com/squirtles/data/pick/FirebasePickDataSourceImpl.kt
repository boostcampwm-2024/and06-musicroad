package com.squirtles.data.pick

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.squirtles.data.firebase.BaseFirebaseDataSource
import com.squirtles.data.firebase.FirebaseCollections
import com.squirtles.data.firebase.FirebaseDocumentFields
import com.squirtles.data.firebase.model.FirebaseFavorite
import com.squirtles.data.firebase.model.FirebasePick
import com.squirtles.data.firebase.model.FirebaseUser
import com.squirtles.data.firebase.model.toPick
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePickDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : BaseFirebaseDataSource(db), FirebasePickDataSource {

    /* Fetches a pick by ID from Firestore */
    override suspend fun fetchPick(pickId: String): Result<FirebasePick> {
        return runCatching {
            val pickSnap = fetchDocumentSnapshot(FirebaseCollections.Picks, pickId).getOrThrow()
            pickSnap.toObject<FirebasePick>()?.copy(id = pickId)!!
        }.onFailure { exception ->
            Log.e(TAG_LOG, "Failed to fetch a pick", exception)
        }
    }

    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): Flow<List<PickWithType>> = callbackFlow {
        val listeners = mutableListOf<ListenerRegistration>()
        try {
            val center = GeoLocation(lat, lng)
            val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)

            bounds.forEach { bound ->
                val listenerRegistration = streamDocumentsInRange(
                    collection = FirebaseCollections.Picks,
                    field = FirebaseDocumentFields.GeoHash,
                    start = bound.startHash,
                    end = bound.endHash,
                ) { snapshot, e ->
                    if (e != null) {
                        Log.w("SnapshotListener", "listen:error", e)
                        throw(e)
                    } else {
                        val pickData = snapshot?.documentChanges
                            ?.filter { isAccurate(it.document, center, radiusInM) }
                            ?.map { dc ->
                                val pick = dc.document.toObject<FirebasePick>().toPick().copy(id = dc.document.id)
                                when (dc.type) {
                                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> PickWithType(PickType.UPDATED, pick)
                                    DocumentChange.Type.REMOVED -> PickWithType(PickType.REMOVED, pick)
                                }
                            }.orEmpty()

                        trySend(pickData)
                    }
                }
                listeners.add(listenerRegistration)
            }
        } catch (e: Exception) {
            close(e)
        }

        // Flow 종료 시 모든 리스너 제거
        awaitClose { listeners.forEach { it.remove() } }
    }

    /* Creates a new pick in Firestore */
    override suspend fun createPick(firebasePick: FirebasePick, userId: String): Result<String> {
        return runCatching {
            val pickRef = addDocument(FirebaseCollections.Picks, firebasePick).getOrThrow()
            updateCurrentUserPick(userId, pickRef.id)
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

    override suspend fun fetchMyPicks(userId: String): Result<List<FirebasePick>> {
        return runCatching {
            val userDocument = fetchDocumentSnapshot(FirebaseCollections.Users, userId).getOrThrow()
            userDocument.toObject<FirebaseUser>()?.myPicks!!.map {
                fetchPick(it).getOrThrow()
            }.reversed()
        }
    }

    override suspend fun fetchFavoritePicks(userId: String): Result<List<FirebasePick>> {
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
            fields = listOf(FirebaseDocumentFields.Uid),
            values = listOf(userId)
        ).getOrThrow().documents
    }

    companion object {
        private const val TAG_LOG = "FirebasePickDataSourceImpl"
    }
}

