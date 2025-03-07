plugins {
    alias(libs.plugins.musicroad.compose.library)
}

android {
    namespace = "com.squirtles.picklist"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.domain.picklist)
    implementation(projects.domain.user)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Coil
    implementation(libs.bundles.coil)
}
