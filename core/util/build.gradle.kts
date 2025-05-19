plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.core.util"
}

dependencies {
    implementation(libs.androidx.navigation.common.ktx)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
