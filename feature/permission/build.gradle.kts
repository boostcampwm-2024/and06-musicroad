plugins {
    alias(libs.plugins.musicroad.feature)
}

android {
    namespace = "com.squirtles.feature.permission"
}

dependencies {
    implementation(libs.accompanist.permissions)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
