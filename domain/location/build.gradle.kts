plugins {
    alias(libs.plugins.musicroad.android.library)
}

android {
    namespace = "com.squirtles.location"
}

dependencies {
    implementation(libs.inject)
}
