package com.squirtles.user

import android.util.Log
import com.squirtles.firebase.model.FirebaseUser
import com.squirtles.firebase.model.toUser
import com.squirtles.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseUserDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : FirebaseUserDataSource {

    override suspend fun createGoogleIdUser(userId: String, userName: String?, userProfileImage: String?): User? {
        return suspendCancellableCoroutine { continuation ->
            val documentReference = db.collection("users").document(userId)
            documentReference.set(FirebaseUser(name = userName, profileImage = userProfileImage))
                .addOnSuccessListener {
                    documentReference.get()
                        .addOnSuccessListener { documentSnapshot ->
                            val savedUser = documentSnapshot.toObject<FirebaseUser>()
                            continuation.resume(
                                savedUser?.toUser()?.copy(userId = documentReference.id)
                            )
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseDataSourceImpl", exception.message.toString())
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun fetchUser(userId: String): User? {
        return suspendCancellableCoroutine { continuation ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val firebaseUser = document.toObject<FirebaseUser>()
                    continuation.resume(firebaseUser?.toUser()?.copy(userId = userId))
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun updateUserName(userId: String, newUserName: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            db.runTransaction { transaction ->
                val userRef = db.collection("users").document(userId)
                val userDocument = transaction.get(userRef)
                transaction.update(userRef, "name", newUserName)

                val myPicks = userDocument.get("myPicks")?.let { it as List<String> } ?: emptyList()
                myPicks.forEach { pickId ->
                    val pickRef = db.collection("picks").document(pickId)
                    transaction.update(pickRef, "createdBy.userName", newUserName)
                }
            }.addOnSuccessListener {
                continuation.resume(true)
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }

}
