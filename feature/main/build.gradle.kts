plugins {
    alias(libs.plugins.musicroad.feature)

}

android {
    namespace = "com.squirtles.feature.main"
}

dependencies {
    implementation(projects.feature.map)
    implementation(projects.feature.permission)
    implementation(projects.feature.create)
    implementation(projects.feature.detail)
    implementation(projects.feature.mypick)
    implementation(projects.feature.favorite)
    implementation(projects.feature.search)
    implementation(projects.feature.userinfo)
    implementation(projects.core.musicplayer)
    implementation(projects.domain.user)
    implementation(projects.domain.firebase)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.firebase.auth.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.test)
}
