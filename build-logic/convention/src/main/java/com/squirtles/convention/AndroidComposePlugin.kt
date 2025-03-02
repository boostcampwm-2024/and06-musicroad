package com.boostcamp.mapisode.convention

import com.android.build.gradle.LibraryExtension
import com.squirtles.convention.extensions.configureComposeAndroid
import com.squirtles.convention.extensions.getLibrary
import com.squirtles.convention.extensions.implementation
import com.squirtles.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposePlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.run {
			pluginManager.run {
				apply("musicroad.android.library")
				apply("org.jetbrains.kotlin.plugin.compose")
			}

			extensions.configure<LibraryExtension> {
				configureComposeAndroid(this)
			}

			dependencies {
				implementation(libs.getLibrary("kotlinx.immutable"))
			}
		}
	}
}
