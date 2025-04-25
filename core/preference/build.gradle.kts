plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.musicroad.hilt)
}

android {
    namespace = "com.squirtles.core.preference"
}

dependencies {
    implementation(projects.domain.preference)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
