package com.squirtles.data.datasource.remote

import android.util.Log
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.squirtles.data.datasource.remote.model.firebase.FirebasePick
import com.squirtles.data.mapper.toPick
import com.squirtles.domain.datasource.FirebaseRemoteDataSource
import com.squirtles.domain.model.Pick
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirebaseRemoteDataSource {

    /**
     * Fetches a pick by ID from Firestore.
     * @param pickID The ID of the pick to fetch.
     * @return  The fetched pick, or null if the pick does not exist on firestore.
     */
    override suspend fun fetchPick(pickID: String): Pick? {
        var resultPick: Pick? = null

        val document = db.collection("picks").document(pickID).get()
            .addOnSuccessListener { document ->
                val firestorePick = document.toObject(FirebasePick::class.java)?.copy(id = pickID)
                Log.d("FirebaseDataSourceImpl", firestorePick.toString())
                resultPick = firestorePick?.toPick()
            }
            .addOnFailureListener { exception ->
                // TODO: Error handling
                throw exception
            }
            .await()

        return resultPick
    }

    /**
     * Fetches picks within a given radius from Firestore.
     * @param lat The latitude of the center of the search area.
     * @param lng The longitude of the center of the search area.
     * @param radiusInM The radius in meters of the search area.
     * @return A list of picks within the specified radius, ordered by distance from the center. can be empty.
     */
    override suspend fun fetchPicksInArea(
        lat: Double,
        lng: Double,
        radiusInM: Double
    ): List<Pick> {
        val center = GeoLocation(lat, lng)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)

        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        val matchingPicks: MutableList<FirebasePick> = ArrayList()

        bounds.forEach { bound ->
            val query = db.collection("picks").orderBy("geoHash").startAt(bound.startHash)
                .endAt(bound.endHash)
            tasks.add(query.get())
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                tasks.forEach { task ->
                    val snap = task.result
                    for (doc in snap.documents) {
                        if (isAccurate(doc, center, radiusInM)) {
                            doc.toObject(FirebasePick::class.java)?.run {
                                matchingPicks.add(this)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // TODO: Error handling
                throw exception
            }

        return matchingPicks.map { it.toPick() }
    }

    /**
     * GeoHash의 FP 문제 - Geohash의 쿼리가 정확하지 않으며 클라이언트 측에서 거짓양성 결과를 필터링해야 합니다.
     * 이러한 추가 읽기로 인해 앱에 비용과 지연 시간이 추가됩니다.
     * @param doc The pick document to check.
     * @param center The center of the search area.
     * @param radiusInM The radius in meters of the search area.
     * @return True if the pick is within the specified radius, false otherwise.
     */
    private fun isAccurate(doc: DocumentSnapshot, center: GeoLocation, radiusInM: Double): Boolean {
        val location = doc.getGeoPoint("location") ?: return false

        val docLocation = GeoLocation(location.latitude, location.longitude)
        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)

        return distanceInM <= radiusInM
    }

    override suspend fun addPick(pick: Pick): Pick {
        TODO("Not yet implemented")
    }

    override suspend fun deletePick(pick: Pick): Boolean {
        TODO("Not yet implemented")
    }
}