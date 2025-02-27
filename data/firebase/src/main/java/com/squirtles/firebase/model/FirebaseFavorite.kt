package com.squirtles.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class FirebaseFavorite(
    val pickId: String? = null,
    val userId: String? = null,
    @ServerTimestamp val addedAt: Timestamp? = null,
)
