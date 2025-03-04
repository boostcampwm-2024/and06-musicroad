package com.squirtles.convention

import com.squirtles.convention.extensions.getBundle
import com.squirtles.convention.extensions.implementation
import com.squirtles.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies


class MusicRoadFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.run {
                apply("musicroad.compose.library")
                apply("musicroad.hilt")
            }

            dependencies {
                implementation(project(":core:model"))
                implementation(project(":core:util"))
                implementation(project(":core:navigation"))

//                implementation(libs.getBundle("compose"))
                implementation(libs.getBundle("navigation"))
            }
        }
    }
}
