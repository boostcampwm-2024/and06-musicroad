plugins {
    alias(libs.plugins.musicroad.android.library)
    alias(libs.plugins.musicroad.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.data"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.buildconfig)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)

    // Firebase
    implementation(libs.bundles.firebase)
    implementation(libs.geofire.android.common)

    // OkHttp
    implementation(libs.bundles.network)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Paging
    implementation(libs.androidx.paging.runtime)
}
