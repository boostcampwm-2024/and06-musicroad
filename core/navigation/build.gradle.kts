plugins {
    alias(libs.plugins.musicroad.java.library)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(projects.core.model)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
