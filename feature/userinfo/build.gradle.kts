plugins {
    alias(libs.plugins.musicroad.feature)
}

android {
    namespace = "com.squirtles.feature.userinfo"
}

dependencies {
    implementation(projects.core.account)
    implementation(projects.core.preference)
    implementation(projects.domain.user)
    implementation(projects.domain.preference)
    implementation(projects.audioVisualizer)

    implementation(libs.coil)
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
