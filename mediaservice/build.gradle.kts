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

    //media3
    implementation(libs.bundles.media3)
    implementation(libs.bundles.exoplayer)
}
