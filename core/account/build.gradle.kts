plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.musicroad.hilt)
}

android {
    namespace = "com.squirtles.account"
}

dependencies {
    implementation(projects.domain.user)
    implementation(projects.core.model)
    implementation(projects.core.buildconfig)

    implementation(libs.bundles.auth)
    implementation(libs.firebase.auth.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Credentials
    implementation(libs.bundles.auth)
}
