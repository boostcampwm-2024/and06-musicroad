package com.squirtles.convention

import com.squirtles.convention.extensions.androidTestImplementation
import com.squirtles.convention.extensions.getBundle
import com.squirtles.convention.extensions.getLibrary
import com.squirtles.convention.extensions.implementation
import com.squirtles.convention.extensions.ksp
import com.squirtles.convention.extensions.kspTest
import com.squirtles.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("dagger.hilt.android.plugin")
                apply("com.google.devtools.ksp")
            }

            dependencies {
                ksp(libs.getLibrary("hilt.android.compiler"))
                kspTest(libs.getLibrary("hilt.android.compiler"))
                implementation(libs.getLibrary("hilt.android"))
                androidTestImplementation(libs.getLibrary("hilt.android.testing"))
            }
        }
    }
}
