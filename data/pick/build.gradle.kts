plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.pick"
}

dependencies {
    implementation(projects.domain.pick)
    implementation(projects.data.firebase)

    // Firebase
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.geofire.android.common)
}
