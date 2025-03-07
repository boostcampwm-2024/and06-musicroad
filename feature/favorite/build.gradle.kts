plugins {
    alias(libs.plugins.musicroad.feature)
}

android {
    namespace = "com.squirtles.favorite"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.picklist)
    implementation(projects.domain.picklist)
    implementation(projects.domain.favorite)
    implementation(projects.domain.pick)
    implementation(projects.domain.order)
    implementation(projects.domain.user)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
