plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.data.order"
}

dependencies {
    implementation(projects.domain.order)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
