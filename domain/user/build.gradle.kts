plugins {
    id(libs.plugins.musicroad.java.library.get().pluginId)
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.domain.picklist)
    implementation(projects.domain.pick)
    implementation(projects.domain.favorite)

    implementation(libs.kotlinx.coroutines.core)
}
