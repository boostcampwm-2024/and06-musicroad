package com.squirtles.convention

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
		with(target) {
			pluginManager.apply("musicroad.android.library")

			extensions.configure<LibraryExtension> {
				configureComposeAndroid(this)
			}

			dependencies {
				implementation(libs.getLibrary("kotlinx.immutable"))
			}
		}
	}
}
