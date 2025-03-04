plugins {
    alias(libs.plugins.musicroad.android.library)
}

android {
    namespace = "com.squirtles.player"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.mediaservice)

    implementation(libs.inject)
    implementation(libs.bundles.media3)
}
