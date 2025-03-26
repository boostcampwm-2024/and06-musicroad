plugins {
    alias(libs.plugins.musicroad.feature)
}

android {
    namespace = "com.squirtles.search"
}

dependencies {
    implementation(projects.domain.applemusic)

    implementation(libs.androidx.paging.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
