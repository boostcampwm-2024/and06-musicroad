plugins {
    alias(libs.plugins.musicroad.java.library)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
