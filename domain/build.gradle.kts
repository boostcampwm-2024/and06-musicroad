plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.musicroad.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.domain"
}

dependencies {

    implementation(project(":mediaservice"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    implementation(libs.androidx.paging.runtime)

    implementation(libs.bundles.media3)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
