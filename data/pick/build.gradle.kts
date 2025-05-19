plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.data.pick"
}

dependencies {
    implementation(projects.domain.pick)
    implementation(projects.data.firebase)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Firebase
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.geofire.android.common)
}
