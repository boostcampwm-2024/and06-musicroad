plugins {
    alias(libs.plugins.musicroad.android.library)
}

android {
    namespace = "com.squirtles.domain.location"
}

dependencies {
    implementation(libs.inject)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
