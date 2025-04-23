package com.squirtles.data.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.squirtles.data.firebase.BaseFirebaseDataSource
import com.squirtles.data.firebase.FirebaseCollections
import com.squirtles.data.firebase.FirebaseDocumentFields
import com.squirtles.data.firebase.model.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : BaseFirebaseDataSource(db), FirebaseUserDataSource {

    override suspend fun createGoogleIdUser(uid: String, newUser: FirebaseUser): Result<FirebaseUser> {
        return runCatching {
            setDocument(FirebaseCollections.Users, uid, newUser)
            newUser
        }.onFailure { e ->
            Log.e(TAG_LOG, e.message.toString())
        }
    }

    override suspend fun fetchUser(uid: String): Result<FirebaseUser> {
        return runCatching {
            val userDocSnap = fetchDocumentSnapshot(FirebaseCollections.Users, uid).getOrThrow()
            userDocSnap.toObject<FirebaseUser>()!!
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
