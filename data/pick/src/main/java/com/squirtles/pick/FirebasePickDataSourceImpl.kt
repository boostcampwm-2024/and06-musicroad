package com.squirtles.pick

import android.util.Log
import com.squirtles.firebase.FirebaseDataSourceConstants.COLLECTION_FAVORITES
import com.squirtles.firebase.FirebaseDataSourceConstants.COLLECTION_PICKS
import com.squirtles.firebase.FirebaseDataSourceConstants.COLLECTION_USERS
import com.squirtles.firebase.FirebaseDataSourceConstants.FIELD_ADDED_AT
import com.squirtles.firebase.FirebaseDataSourceConstants.FIELD_MY_PICKS
import com.squirtles.firebase.FirebaseDataSourceConstants.FIELD_PICK_ID
import com.squirtles.firebase.FirebaseDataSourceConstants.FIELD_USER_ID
import com.squirtles.firebase.FirebaseDataSourceConstants.TAG_LOG
import com.squirtles.firebase.model.FirebasePick
import com.squirtles.firebase.model.FirebaseUser
import com.squirtles.firebase.model.toFirebasePick
import com.squirtles.firebase.model.toPick
import com.squirtles.model.Pick
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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebasePickDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirebasePickDataSource {

    /* Fetches a pick by ID from Firestore */
    override suspend fun fetchPick(pickID: String): Pick? {
        return suspendCancellableCoroutine { continuation ->
            db.collection("picks").document(pickID).get()
                .addOnSuccessListener { document ->
                    val firestorePick = document.toObject<FirebasePick>()?.copy(id = pickID)
                    val resultPick = firestorePick?.toPick()
                    continuation.resume(resultPick)
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseDataSourceImpl", "Failed to fetch a pick", exception)
                    continuation.resumeWithException(exception)
                }
        }
    }

    /* Fetches picks within a given radius from Firestore */
    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): List<Pick> {
        val center = GeoLocation(lat, lng)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)

        val queries: MutableList<Query> = ArrayList()
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        val matchingPicks: MutableList<Pick> = ArrayList()

        bounds.forEach { bound ->
            val query = db.collection("picks")
                .orderBy("geoHash")
                .startAt(bound.startHash)
                .endAt(bound.endHash)
            queries.add(query)
        }

        try {
            queries.forEach { query ->
                tasks.add(query.get())
            }
            Tasks.whenAllComplete(tasks).await()
        } catch (exception: Exception) {
            Log.e("FirebaseDataSourceImpl", "Failed to fetch picks", exception)
            throw exception
        }

        tasks.forEach { task ->
            val snap = task.result
            snap.documents.forEach { doc ->
                if (isAccurate(doc, center, radiusInM)) {
                    doc.toObject<FirebasePick>()?.run {
                        matchingPicks.add(this.toPick().copy(id = doc.id))
                    }
                }
            }
        }

        return matchingPicks
    }

    /* Creates a new pick in Firestore */
    override suspend fun createPick(pick: Pick): String =
        suspendCancellableCoroutine { continuation ->
            val firebasePick = pick.toFirebasePick()

            // add() 메소드는 Cloud Firestore에서 ID를 자동으로 생성
            db.collection("picks").add(firebasePick)
                .addOnSuccessListener { documentReference ->
                    val pickId = documentReference.id
                    // 유저의 픽 정보 업데이트
                    updateCurrentUserPick(pick.createdBy.userId, pickId)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                continuation.resume(pickId)
                            } else {
                                continuation.resumeWithException(
                                    task.exception ?: Exception("Failed to updating user pick info")
                                )
                            }
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseDataSourceImpl", "Failed to create a pick", exception)
                    continuation.resumeWithException(exception)
                }
        }

    override suspend fun deletePick(pickId: String, userId: String): Boolean {
        val pickDocument = db.collection(COLLECTION_PICKS).document(pickId)
        val userDocument = db.collection(COLLECTION_USERS).document(userId)
        val favoriteDocuments = fetchFavoriteDocuments(pickId)

        return suspendCancellableCoroutine { continuation ->
            db.runTransaction { transaction ->
                transaction.delete(pickDocument)

                favoriteDocuments.forEach { document ->
                    transaction.delete(document)
                }

                transaction.update(userDocument, FIELD_MY_PICKS, FieldValue.arrayRemove(pickId))
            }.addOnSuccessListener { _ ->
                continuation.resume(true)
            }.addOnFailureListener { e ->
                Log.w(TAG_LOG, "Transaction failure.", e)
                continuation.resumeWithException(e)
            }
        }
    }

    override suspend fun fetchMyPicks(userId: String): List<Pick> {
        val userDocument = fetchUserDocument(userId)
        if (userDocument.exists().not()) throw Exception("No user info in database")

        val tasks = mutableListOf<Task<DocumentSnapshot>>()
        val myPicks = mutableListOf<Pick>()

        try {
            userDocument.toObject<FirebaseUser>()?.myPicks?.forEach { pickId ->
                tasks.add(
                    db.collection(COLLECTION_PICKS)
                        .document(pickId)
                        .get()
                )
            }
            Tasks.whenAllComplete(tasks).await()
        } catch (exception: Exception) {
            Log.e("FirebaseDataSourceImpl", "Failed to fetch my picks", exception)
            throw exception
        }

        tasks.forEach { task ->
            task.result.toObject<FirebasePick>()?.run {
                myPicks.add(this.toPick().copy(id = task.result.id))
            }
        }

        return myPicks.reversed()
    }

    private fun updateCurrentUserPick(userId: String, pickId: String): Task<Void> {
        val userDoc = db.collection("users").document(userId)
        return userDoc.update("myPicks", FieldValue.arrayUnion(pickId))
    }

    private suspend fun fetchFavoriteDocuments(pickId: String): List<DocumentReference> {
        return suspendCancellableCoroutine { continuation ->
            db.collection(COLLECTION_FAVORITES)
                .whereEqualTo(FIELD_PICK_ID, pickId)
                .get()
                .addOnSuccessListener { querySnapShot ->
                    val documentIds = querySnapShot.documents.map { it.id }
                    val documentRefs = mutableListOf<DocumentReference>()
                    documentIds.forEach { id ->
                        documentRefs.add(db.collection(COLLECTION_FAVORITES).document(id))
                    }
                    continuation.resume(documentRefs)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG_LOG, "Failed to fetch favorite documents id", e)
                    continuation.resumeWithException(e)
                }
        }
    }

    private suspend fun fetchUserDocument(userId: String): DocumentSnapshot {
        return suspendCancellableCoroutine { continuation ->
            db.collection(COLLECTION_USERS).document(userId)
                .get()
                .addOnSuccessListener { document ->
                    continuation.resume(document)
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseDataSourceImpl", "Failed to get user document", exception)
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun fetchFavoritePicks(userId: String): List<Pick> {
        val favoriteDocuments = fetchFavoritesByUserId(userId)

        val tasks = mutableListOf<Task<DocumentSnapshot>>()
        val favorites = mutableListOf<Pick>()

        try {
            favoriteDocuments.forEach { doc ->
                tasks.add(
                    db.collection(COLLECTION_PICKS)
                        .document(doc.data[FIELD_PICK_ID].toString())
                        .get()
                )
            }
            Tasks.whenAllComplete(tasks).await()
        } catch (exception: Exception) {
            Log.e("FirebaseDataSourceImpl", "Failed to get favorite picks", exception)
            throw exception
        }
        tasks.forEach { task ->
            task.result.toObject<FirebasePick>()?.run {
                favorites.add(this.toPick().copy(id = task.result.id))
            }
        }

        return favorites
    }

    /**
     * GeoHash의 FP 문제 - Geohash의 쿼리가 정확하지 않으며 클라이언트 측에서 거짓양성 결과를 필터링해야 합니다.
     * 이러한 추가 읽기로 인해 앱에 비용과 지연 시간이 추가됩니다.
     */
    private fun isAccurate(doc: DocumentSnapshot, center: GeoLocation, radiusInM: Double): Boolean {
        val location = doc.getGeoPoint("location") ?: return false

        val docLocation = GeoLocation(location.latitude, location.longitude)
        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)

        return distanceInM <= radiusInM
    }

    private suspend fun fetchFavoritesByUserId(userId: String): QuerySnapshot {
        val query = db.collection(COLLECTION_FAVORITES)
            .whereEqualTo(FIELD_USER_ID, userId)
            .orderBy(FIELD_ADDED_AT, Query.Direction.DESCENDING)

        return executeQuery(query)
    }

    private suspend fun executeQuery(query: Query): QuerySnapshot {
        return suspendCancellableCoroutine { continuation ->
            query.get()
                .addOnSuccessListener { result ->
                    continuation.resume(result)
                }
                .addOnFailureListener { exception ->
                    Log.w("FirebaseDataSourceImpl", "Error fetching favorite documents", exception)
                    continuation.resumeWithException(exception)
                }
        }
    }
}
