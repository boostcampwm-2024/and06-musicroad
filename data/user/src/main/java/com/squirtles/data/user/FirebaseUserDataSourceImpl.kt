package com.squirtles.data.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.squirtles.data.firebase.BaseFirebaseDataSource
import com.squirtles.data.firebase.FirebaseCollections
import com.squirtles.data.firebase.FirebaseDocumentFields
import com.squirtles.data.firebase.model.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
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
            val userDocSnap = fetchDocumentSnapshot(FirebaseCollections.Users, uid).getOrThrow()
            val profileImageUrl = userDocSnap.getString(FirebaseDocumentFields.ProfileImage.name)
            profileImageUrl?.let { url ->
                deleteImageFromStorage(url).runCatching {
                }.onFailure { e ->
                    Log.w(TAG_LOG, "Failed to delete profile image from Storage for uid: $uid", e)
                }
            }

            FirebaseAuth.getInstance().currentUser?.delete()?.await()
            return deleteDocument(FirebaseCollections.Users, uid)
        }.onFailure {
            Log.e(TAG_LOG, "Failed to delete a user", it)
        }
    }

    private suspend fun deleteImageFromStorage(imageUrl: String) {
        try {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
        } catch (e: Exception) {
            Log.w(TAG_LOG, "No image found in storage for url: $imageUrl", e)
        }
    }

    override suspend fun updateUserProfileImage(uid: String, imageData: ByteArray): Result<Boolean> {
        return runCatching {
            val fileName = "profile_images/${uid}_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child(fileName)

            storageRef.putBytes(imageData).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            val userDocRef = db.collection(FirebaseCollections.Users.name).document(uid)

            try {
                var oldImageUrl: String? = null
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(userDocRef)
                    oldImageUrl = snapshot.getString(FirebaseDocumentFields.ProfileImage.name)
                    transaction.update(userDocRef, FirebaseDocumentFields.ProfileImage.name, downloadUrl)
                }.await()

                oldImageUrl?.let { url -> deleteImageFromStorage(url) }
            } catch (dbError: Exception) {
                storageRef.delete().await()
                throw dbError
            }

            true
        }.onFailure { e ->
            Log.e(TAG_LOG, "Failed to update user profile image", e)
        }
    }

    override suspend fun deleteUserProfileImage(uid: String): Result<Boolean> {
        return runCatching {
            val userDocSnap = fetchDocumentSnapshot(FirebaseCollections.Users, uid).getOrThrow()
            val currentProfileImageUrl = userDocSnap.getString(FirebaseDocumentFields.ProfileImage.name)

            currentProfileImageUrl?.let { deleteImageFromStorage(it) }
            db.runTransaction { transaction ->
                transaction.update(userDocSnap.reference, FirebaseDocumentFields.ProfileImage.name, null)
            }

            true
        }.onFailure { e ->
            Log.e(TAG_LOG, "Failed to delete user profile image", e)
        }
    }

    companion object {
        const val TAG_LOG = "FirebaseUserDataSourceImpl"
    }
}
