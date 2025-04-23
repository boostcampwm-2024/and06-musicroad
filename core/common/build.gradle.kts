plugins {
    alias(libs.plugins.musicroad.compose.library)
}

android {
    namespace = "com.squirtles.core.common"
}

dependencies {

    // Compose
//    implementation(platform(libs.compose.bom))
//    implementation(libs.compose.runtime.android)
//    implementation(libs.compose.ui.tooling.preview)
//    implementation(platform(libs.compose.bom))
//    implementation(libs.bundles.compose)
//    implementation(libs.bundles.compose.debug)
//    implementation(libs.bundles.material)

    // Coil
    implementation(libs.bundles.coil)
}
