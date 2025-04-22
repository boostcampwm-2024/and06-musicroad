plugins {
    alias(libs.plugins.musicroad.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.detail"
}

dependencies {
    implementation(projects.audioVisualizer)
    implementation(projects.core.account)
    implementation(projects.core.musicplayer)
    implementation(projects.domain.pick)
    implementation(projects.domain.picklist)
    implementation(projects.domain.user)
    implementation(projects.domain.favorite)

    implementation(libs.coil.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.googleid)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
