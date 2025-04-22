plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.musicroad.hilt)
}

android {
    namespace = "com.squirtles.location"
}

dependencies {
    implementation(projects.domain.location)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
