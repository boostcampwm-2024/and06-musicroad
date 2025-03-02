plugins {
    alias(libs.plugins.musicroad.android.library)
}

android {
    namespace = "com.squirtles.picklist"
    compileSdk = 34

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.compose.debug)

    implementation(libs.bundles.material)

    // Coil
    implementation(libs.bundles.coil)
}
