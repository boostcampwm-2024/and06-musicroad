package com.squirtles.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.squirtles.firebase.BaseFirebaseDataSource
import com.squirtles.firebase.FirebaseCollections
import com.squirtles.firebase.FirebaseDocumentFields
import com.squirtles.firebase.model.FirebaseUser
import com.squirtles.firebase.model.toUser
import com.squirtles.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : BaseFirebaseDataSource(db), FirebaseUserDataSource {

    override suspend fun createGoogleIdUser(
        uid: String,
        email: String,
        userName: String?,
        userProfileImage: String?
    ): Result<User> {
        return runCatching {
            val newUser = FirebaseUser(email = email, name = userName, profileImage = userProfileImage)
            setDocument(FirebaseCollections.Users, uid, newUser)
            newUser.toUser().copy(uid = uid)
        }.onFailure { e ->
            Log.e(TAG_LOG, e.message.toString())
        }
    }

    override suspend fun fetchUser(uid: String): Result<User> {
        return runCatching {
            val userDocSnap = fetchDocumentSnapshot(FirebaseCollections.Users, uid).getOrThrow()
            userDocSnap.toObject<FirebaseUser>()?.toUser()?.copy(uid = uid)!!
        }.onFailure { e ->
            Log.e(TAG_LOG, "Failed to fetch a user", e)
        }
    }

    override suspend fun updateUserName(uid: String, newUserName: String): Result<Boolean> {
        return runCatching {
            val userDocSnap = fetchDocumentSnapshot(FirebaseCollections.Users, uid).getOrThrow()
            db.runTransaction { transaction ->
                transaction.update(userDocSnap.reference, FirebaseDocumentFields.Name.name, newUserName)

                val myPicks = userDocSnap.toObject<FirebaseUser>()?.myPicks
                requireNotNull(myPicks)
                myPicks.forEach { pickId ->
                    val pickRef = fetchDocumentReference(FirebaseCollections.Picks, pickId)
                    transaction.update(pickRef, FirebaseDocumentFields.CreatedUserName.name, newUserName)
                }
            }
            true
        }.onFailure { e ->
            Log.e(TAG_LOG, "Failed to update a user name", e)
        }
    }

    override suspend fun deleteUser(uid: String): Result<Void> {
        return runCatching {
            FirebaseAuth.getInstance().currentUser?.delete()?.await()
            return deleteDocument(FirebaseCollections.Users, uid)
        }.onFailure {
            Log.e(TAG_LOG, "Failed to delete a user", it)
        }
    }

    companion object {
        const val TAG_LOG = "FirebaseUserDataSourceImpl"
    }
}
