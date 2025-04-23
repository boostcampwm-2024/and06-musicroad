package com.squirtles.core.buildconfig

import com.squirtles.localproperties.BuildConfig

object LocalPropertyProvider {
    val googleClientId: String
        get() = BuildConfig.GOOGLE_CLIENT_ID

    val appleMusicApiToken: String
        get() = BuildConfig.APPLE_MUSIC_API_TOKEN

    val firestoreDbId: String
        get() = BuildConfig.FIRESTORE_DB_ID

    val httpsCallable: String
        get() = BuildConfig.HTTPS_CALLABLE
}
