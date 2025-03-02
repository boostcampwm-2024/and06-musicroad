plugins {
    `kotlin-dsl`
}

group = "com.squirtles.build-logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "musicroad.android.application"
            implementationClass = "com.squirtles.convention.AndroidApplicationPlugin"
        }

        register("androidLibrary") {
            id = "musicroad.android.library"
            implementationClass = "com.squirtles.convention.AndroidLibraryPlugin"
        }

        register("androidCompose") {
            id = "musicroad.android.compose"
            implementationClass = "com.squirtles.convention.AndroidComposePlugin"
        }

        register("javaLibrary") {
            id = "musicroad.java.library"
            implementationClass = "com.squirtles.convention.JavaLibraryPlugin"
        }

        register("hilt") {
            id = "musicroad.hilt"
            implementationClass = "com.squirtles.convention.HiltPlugin"
        }

        register("data") {
            id = "musicroad.data"
            implementationClass = "com.squirtles.convention.MusicRoadDataPlugin"
        }
    }
}
