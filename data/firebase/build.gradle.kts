plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.data.firebase"
}

dependencies {
    implementation(projects.domain.firebase)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.geofire.android.common)
}
