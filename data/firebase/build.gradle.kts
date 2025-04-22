plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.firebase"
}

dependencies {
    implementation(projects.domain.firebase)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Firebase
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.geofire.android.common)
}
