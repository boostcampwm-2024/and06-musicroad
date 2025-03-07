package com.squirtles.data.user.model

data class FirebaseUser(
    val email: String? = null,
    val name: String? = null,
    val profileImage: String? = null,
    val myPicks: List<String> = emptyList()
)
