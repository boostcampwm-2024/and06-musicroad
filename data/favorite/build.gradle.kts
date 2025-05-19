plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.data.favorite"
}

dependencies {
    implementation(projects.data.firebase)
    implementation(projects.domain.firebase)
    implementation(projects.domain.favorite)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Firebase
    implementation(libs.bundles.firebase)
}
