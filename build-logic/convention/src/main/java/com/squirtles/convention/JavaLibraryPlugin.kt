package com.squirtles.convention

import com.squirtles.convention.extensions.getLibrary
import com.squirtles.convention.extensions.getVersion
import com.squirtles.convention.extensions.implementation
import com.squirtles.convention.extensions.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class JavaLibraryPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			pluginManager.apply {
				apply("org.jetbrains.kotlin.jvm")
				apply("java-library")
			}

			extensions.configure<JavaPluginExtension> {
				sourceCompatibility = JavaVersion.VERSION_17
				targetCompatibility = JavaVersion.VERSION_17
			}

			extensions.configure<KotlinProjectExtension> {
				jvmToolchain(libs.getVersion("jdkVersion").requiredVersion.toInt())
			}

			dependencies {
				implementation(libs.getLibrary("inject"))
			}
		}
	}
}
