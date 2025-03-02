plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.user"
}

dependencies {
    implementation(projects.domain.user)
    implementation(projects.data.firebase)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // firebase
    implementation(libs.firebase.firestore.ktx)
}
