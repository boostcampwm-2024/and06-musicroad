plugins {
    alias(libs.plugins.musicroad.feature)
}

android {
    namespace = "com.squirtles.feature.permission"
}

dependencies {

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
