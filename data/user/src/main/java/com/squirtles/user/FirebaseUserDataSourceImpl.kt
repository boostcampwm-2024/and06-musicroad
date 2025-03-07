package com.squirtles.user

import android.util.Log
import com.squirtles.firebase.model.FirebaseUser
import com.squirtles.firebase.model.toUser
import com.squirtles.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.squirtles.firebase.BaseFirebaseDataSource
import com.squirtles.firebase.FirebaseCollections
import com.squirtles.firebase.FirebaseDocumentFields
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseUserDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : BaseFirebaseDataSource(db), FirebaseUserDataSource {

    override suspend fun createGoogleIdUser(
        userId: String,
        userName: String?,
        userProfileImage: String?
    ): Result<User> {
        return runCatching {
            val firebaseUser = FirebaseUser(name = userName, profileImage = userProfileImage)
            setDocument(FirebaseCollections.Users, userId, firebaseUser)

            val docSnap = fetchDocumentSnapshot(FirebaseCollections.Users, userId).getOrThrow()
            docSnap.toObject<FirebaseUser>()?.toUser()!!
        }.onFailure { e ->
            Log.e(TAG_LOG, e.message.toString())
        }
    }

    override suspend fun fetchUser(userId: String): Result<User> {
        return runCatching {
            val docSnap = fetchDocumentSnapshot(FirebaseCollections.Users, userId).getOrThrow()
            docSnap.toObject<FirebaseUser>()?.toUser()!!
        }.onFailure { e ->
            Log.e(TAG_LOG, "Failed to fetch a user", e)
        }
    }

    override suspend fun updateUserName(userId: String, newUserName: String): Result<Boolean> {
        return runCatching {
            val userSnap = fetchDocumentSnapshot(FirebaseCollections.Users, userId).getOrThrow()

            db.runTransaction { transaction ->
                transaction.update(userSnap.reference, FirebaseDocumentFields.Name.name, newUserName)
                val myPicks = userSnap.get(FirebaseDocumentFields.MyPicks.name)?.let { it as List<String> } ?: emptyList()

                // 해당 유저의 모든 pick의 등록 유저 정보 업데이트
                myPicks.forEach { pickId ->
                    val pickRef = fetchDocumentReference(FirebaseCollections.Picks, pickId)
                    transaction.update(pickRef, FirebaseDocumentFields.CreatedUserName.name, newUserName)
                }
            }.await()

            true
        }.onFailure {
            Log.e(TAG_LOG, "Failed to update a user name", it)
        }
    }

    companion object {
        private const val TAG_LOG = "FirebaseUserDataSourceImpl"
    }
}
