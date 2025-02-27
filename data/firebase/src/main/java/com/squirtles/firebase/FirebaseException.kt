package com.squirtles.firebase

sealed class FirebaseException(override val message: String) : Exception() {
    data class CreatedUserFailedException(override val message: String = "Failed to create a user") :
        FirebaseException(message)

    data class UserNotFoundException(override val message: String = "Failed to fetch a user") :
        FirebaseException(message)

    data class NoSuchPickException(override val message: String = "No such pick") : FirebaseException(message)
    data class NoSuchPickInRadiusException(override val message: String = "No such pick in area") :
        FirebaseException(message)
}
