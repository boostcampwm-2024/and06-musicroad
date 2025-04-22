package com.squirtles.firebase

import com.squirtles.domain.firebase.FirebaseException

suspend fun <T> handleResult(
    firebaseRepositoryException: FirebaseException,
    call: suspend () -> T?
): Result<T> {
    return runCatching {
        call() ?: throw firebaseRepositoryException
    }
}

suspend fun <T> handleResult(
    call: suspend () -> T
): Result<T> {
    return runCatching {
        call()
    }
}

