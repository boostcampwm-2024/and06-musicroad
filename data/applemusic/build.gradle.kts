plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.applemusic"
}

dependencies {
    implementation(projects.domain.applemusic)

    implementation(libs.androidx.paging.runtime)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // OkHttp
    implementation(libs.bundles.network)
}
