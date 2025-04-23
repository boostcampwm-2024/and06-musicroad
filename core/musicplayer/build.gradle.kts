plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.musicroad.hilt)
}

android {
    namespace = "com.squirtles.core.musicplayer"
}

dependencies {
    implementation(projects.domain.player)
    implementation(projects.core.model)
    implementation(libs.material)

    // ExoPlayer
    implementation(libs.bundles.exoplayer)
    implementation(libs.androidx.media3.session)
}
