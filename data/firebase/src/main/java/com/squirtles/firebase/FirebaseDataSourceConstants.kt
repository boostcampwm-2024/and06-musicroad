package com.squirtles.firebase

sealed class FirebaseCollections(val name: String) {
    data object Favorites: FirebaseCollections("favorites")
    data object Picks: FirebaseCollections("picks")
    data object Users: FirebaseCollections("users")
}

sealed class FirebaseDocumentFields(val name: String) {
    data object AddedAt: FirebaseDocumentFields("addedAt")
    data object PickId: FirebaseDocumentFields("pickId")
    data object Uid: FirebaseDocumentFields("uid")
    data object MyPicks: FirebaseDocumentFields("myPicks")
    data object Name: FirebaseDocumentFields("name")
    data object Location: FirebaseDocumentFields("location")
    data object GeoHash: FirebaseDocumentFields("geoHash")
    data object CreatedUserName: FirebaseDocumentFields("createdBy.userName")
}
