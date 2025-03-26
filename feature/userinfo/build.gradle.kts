plugins {
    alias(libs.plugins.musicroad.feature)
}

android {
    namespace = "com.squirtles.userinfo"
}

dependencies {
    implementation(projects.core.account)
    implementation(projects.domain.user)

    implementation(libs.coil)
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
