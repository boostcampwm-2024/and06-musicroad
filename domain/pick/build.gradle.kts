plugins {
    id(libs.plugins.musicroad.java.library.get().pluginId)
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.domain.picklist)

    implementation(libs.kotlinx.coroutines.core)
}
