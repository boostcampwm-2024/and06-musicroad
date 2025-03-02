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

    // Credentials
    implementation(libs.bundles.auth)
}
