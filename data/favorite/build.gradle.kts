plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.favorite"
}

dependencies {
    implementation(projects.data.firebase)
    implementation(projects.domain.favorite)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Firebase
    implementation(libs.bundles.firebase)
}
