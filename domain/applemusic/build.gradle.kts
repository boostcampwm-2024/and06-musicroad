plugins {
    alias(libs.plugins.musicroad.android.library)
}

android {
    namespace = "com.squirtles.applemusic"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.inject)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
