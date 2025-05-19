package com.squirtles.convention

import com.squirtles.convention.extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class MusicRoadDataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("musicroad.android.library")
                apply("musicroad.hilt")
            }

            dependencies {
                implementation(project(":core:model"))
                implementation(project(":core:buildconfig"))
            }
        }
    }
}
