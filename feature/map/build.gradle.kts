plugins {
    alias(libs.plugins.musicroad.feature)
}

android {
    namespace = "com.squirtles.feature.map"
}

dependencies {
    implementation(projects.core.account)
    implementation(projects.core.musicplayer)
    implementation(projects.domain.pick)
    implementation(projects.domain.location)
    implementation(projects.domain.user)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Map
    implementation(libs.map.sdk)
    implementation(libs.play.services.location)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.auth)
}
