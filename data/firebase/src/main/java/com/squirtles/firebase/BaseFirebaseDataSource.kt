package com.squirtles.firebase

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.squirtles.domain.firebase.FirebaseException
import kotlinx.coroutines.tasks.await

open class BaseFirebaseDataSource(
    private val db: FirebaseFirestore
) {
    protected fun fetchCollection(collection: FirebaseCollections): CollectionReference = db.collection(collection.name)

    protected fun fetchDocumentReference(collection: FirebaseCollections, documentId: String): DocumentReference =
        fetchCollection(collection).document(documentId)

    protected suspend fun fetchDocumentSnapshot(collection: FirebaseCollections, documentId: String): Result<DocumentSnapshot> {
        return runCatching {
            fetchCollection(collection).document(documentId).get().await().ifNotExist {
                throw FirebaseException.NoSuchDocumentException(docId = documentId, collection = collection.name)
            }
        }.onFailure {
            Log.e("FirebaseDataSource", "Failed to fetch document snapshot", it)
            throw FirebaseException.FetchDocumentFailedException(collection = collection.name)
        }
    }

    protected suspend fun queryDocumentsEquals(
        collection: FirebaseCollections,
        fields: List<FirebaseDocumentFields>,
        values: List<Any>
    ): Result<QuerySnapshot> {
        return runCatching {
            var query: Query = fetchCollection(collection)
            fields.forEachIndexed { index, field ->
                query = query.whereEqualTo(field.name, values[index])
            }
            query.get().await()
        }.onFailure {
            Log.e("FirebaseDataSource", "Failed to query documents", it)
            throw FirebaseException.ExecuteQueryFailedException(collection = collection.name)
        }
    }

    protected suspend fun queryDocumentsInRange(
        collection: FirebaseCollections,
        field: FirebaseDocumentFields,
        start: String,
        end: String
    ): Result<QuerySnapshot> {
        return runCatching {
            fetchCollection(collection)
                .whereGreaterThanOrEqualTo(field.name, start)
                .whereLessThanOrEqualTo(field.name, end)
                .get()
                .await()
        }.onFailure {
            Log.e("FirebaseDataSource", "Failed to create query for range", it)
            throw FirebaseException.ExecuteQueryFailedException(collection = collection.name)
        }
    }

    protected suspend fun updateDocument(
        collection: FirebaseCollections,
        documentId: String,
        field: FirebaseDocumentFields,
        value: Any
    ): Result<Void> {
        return runCatching {
            fetchDocumentReference(collection, documentId).update(field.name, value).await()
        }.onFailure {
            Log.e("FirebaseDataSource", "Failed to update document", it)
            throw FirebaseException.UpdateDocumentFailedException(docId = documentId, collection = collection.name)
        }
    }

    protected suspend fun setDocument(collection: FirebaseCollections, docId:String, value: Any): Result<Void> {
        return runCatching {
            fetchDocumentReference(collection, docId).set(value).await()
        }.onFailure {
            Log.e("FirebaseDataSource", "Failed to set document", it)
            throw FirebaseException.AddDocumentFailedException(value = value, collection = collection.name)
        }
    }

    protected suspend fun addDocument(collection: FirebaseCollections, value: Any): Result<DocumentReference> {
        return runCatching {
            fetchCollection(collection).add(value).await()
        }.onFailure {
            Log.e("FirebaseDataSource", "Failed to add document", it)
            throw FirebaseException.AddDocumentFailedException(value = value, collection = collection.name)
        }
    }

    protected suspend fun deleteDocument(document: DocumentReference, collectionName: String = ""): Result<Void> {
        return runCatching {
            document.delete().await()
        }.onFailure { exception ->
            Log.e("FirebaseDataSource", "Error deleting favorite document with ID: ${document.id}", exception)
            throw FirebaseException.DeleteDocumentFailedException(docId = document.id, collection = collectionName)
        }
    }

    protected suspend fun deleteDocument(collection: FirebaseCollections, documentId: String): Result<Void> {
        val doc = fetchDocumentReference(collection, documentId)
        return deleteDocument(doc, collection.name)
    }

    private fun DocumentSnapshot.ifNotExist(action: () -> Unit): DocumentSnapshot {
        if (exists().not()) action()
        return this
    }
}
