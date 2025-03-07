package com.squirtles.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class FirebaseFavorite(
    val pickId: String? = null,
    val uid: String? = null,
    @ServerTimestamp val addedAt: Timestamp? = null,
)
