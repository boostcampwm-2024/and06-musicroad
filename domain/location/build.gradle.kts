plugins {
    id(libs.plugins.musicroad.java.library.get().pluginId)
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)
    
    testImplementation(libs.junit)
}
