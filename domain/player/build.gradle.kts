plugins {
    alias(libs.plugins.musicroad.android.library)
}

android {
    namespace = "com.squirtles.domain.player"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.mediaservice)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    implementation(libs.inject)
    implementation(libs.bundles.media3)
}
