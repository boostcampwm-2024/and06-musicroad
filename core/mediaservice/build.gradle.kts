plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.musicroad.hilt)
}

android {
    namespace = "com.squirtles.mediaservice"
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
    // ExoPlayer
    implementation(libs.bundles.exoplayer)
    implementation(libs.androidx.media3.session)
}
