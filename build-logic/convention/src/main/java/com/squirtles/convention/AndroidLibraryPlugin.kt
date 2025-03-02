package com.squirtles.convention

import com.android.build.gradle.LibraryExtension
import com.squirtles.convention.extensions.configureKotlinAndroid
import com.squirtles.convention.extensions.configureKotlinCoroutine
import com.squirtles.convention.extensions.getBundle
import com.squirtles.convention.extensions.getLibrary
import com.squirtles.convention.extensions.implementation
import com.squirtles.convention.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			pluginManager.apply("com.android.library")

			extensions.configure<LibraryExtension> {
				configureKotlinAndroid(this)
				configureKotlinCoroutine(this)
			}

			dependencies {
				implementation(libs.getBundle("androidx-core"))
			}
		}
	}
}
