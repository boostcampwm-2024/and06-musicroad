plugins {
    alias(libs.plugins.musicroad.data)
}

android {
    namespace = "com.squirtles.data.preference"
}

dependencies {
    implementation(projects.domain.preference)

    // Datastore
    implementation(libs.androidx.datastore.preferences)
}
