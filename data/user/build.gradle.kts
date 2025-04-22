plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.user"
}

dependencies {
    implementation(projects.domain.user)
    implementation(projects.data.firebase)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // firebase
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
}
