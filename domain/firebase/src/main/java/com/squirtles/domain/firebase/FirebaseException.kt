package com.squirtles.domain.firebase

sealed class FirebaseException(override val message: String) : Exception() {
    data class CreatedUserFailedException(override val message: String = "Failed to create user") : FirebaseException(message)
    data class FetchUserFailedException(override val message: String = "Failed to fetch user") : FirebaseException(message)
    data class UpdateUserFailedException(override val message: String = "Failed to update user info") : FirebaseException(message)
    data class NoSuchPickException(override val message: String = "No such pick", val pickId: String)
        : FirebaseException("message @$pickId")
    data class NoSuchPickInRadiusException(override val message: String = "No such pick in area") : FirebaseException(message)

    data class NoSuchDocumentException(
        override val message: String = "No such document",
        val docId: String,
        val collection: String = ""
    ) : FirebaseException("$message @$docId in $collection")

    data class FetchDocumentFailedException(
        override val message: String = "Failed to fetch document",
        val collection: String = ""
    ) : FirebaseException("$message in $collection")

    data class AddDocumentFailedException(
        override val message: String = "Failed to add document",
        val value: Any,
        val collection: String = ""
    ) : FirebaseException("$message $value in $collection")

    data class DeleteDocumentFailedException(
        override val message: String = "Failed to delete document",
        val docId: String,
        val collection: String = ""
    ): FirebaseException("$message @$docId in $collection")

    data class UpdateDocumentFailedException(
        override val message: String = "Failed to update document",
        val docId: String,
        val collection: String = ""
    ) : FirebaseException("$message @$docId in $collection")

    data class ExecuteQueryFailedException(
        override val message: String = "Failed to execute query",
        val collection: String = ""
    ) : FirebaseException("$message in $collection")

    data class CloudFunctionFailedException(
        override val message: String = "Failed to run cloud function",
        val exceptionMessage: String
    ) : FirebaseException("$message : $exceptionMessage")
}
