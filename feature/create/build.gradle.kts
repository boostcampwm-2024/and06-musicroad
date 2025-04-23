plugins {
    alias(libs.plugins.musicroad.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.feature.create"
}

dependencies {
    implementation(projects.core.common)

    implementation(projects.domain.pick)
    implementation(projects.domain.user)
    implementation(projects.domain.applemusic)
    implementation(projects.domain.location)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
