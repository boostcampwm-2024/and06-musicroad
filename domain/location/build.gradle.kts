plugins {
    alias(libs.plugins.musicroad.android.library)
}

android {
    namespace = "com.squirtles.domain.location"
}

dependencies {
    implementation(libs.inject)
    implementation(projects.core.model)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
