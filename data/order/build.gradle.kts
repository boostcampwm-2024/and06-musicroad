plugins {
    id(libs.plugins.musicroad.data.get().pluginId)
}

android {
    namespace = "com.squirtles.order"
}

dependencies {
    implementation(projects.domain.order)
}
