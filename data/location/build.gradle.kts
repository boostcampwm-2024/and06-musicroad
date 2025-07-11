plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.musicroad.hilt)
}

android {
    namespace = "com.squirtles.data.location"
}

dependencies {
    implementation(projects.domain.location)
    implementation(projects.core.model)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
