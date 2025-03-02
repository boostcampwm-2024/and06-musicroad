plugins {
    alias(libs.plugins.musicroad.android.library)
//    alias(libs.plugins.musicroad.compose.library)
}

android {
    namespace = "com.squirtles.common"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {

    // Compose
//    implementation(platform(libs.compose.bom))
//    implementation(libs.compose.runtime.android)
//    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.material)

    // Coil
    implementation(libs.bundles.coil)
}
