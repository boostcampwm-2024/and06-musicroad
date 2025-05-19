package com.squirtles.core.model

data class User(
    val uid: String,
    val email: String,
    val userName: String,
    val userProfileImage: String?,
    val myPicks: List<String>,
)
