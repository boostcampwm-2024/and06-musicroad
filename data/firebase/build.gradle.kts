plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.firebase"
}

dependencies {
    // Firebase
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.geofire.android.common)
}
