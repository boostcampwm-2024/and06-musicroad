package com.squirtles.convention.extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureComposeAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
	pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

	commonExtension.apply {
		buildFeatures {
			compose = true
		}

		composeOptions {
			kotlinCompilerExtensionVersion = libs.getVersion("compose-compiler").requiredVersion
		}

		dependencies {
			val composeBom = libs.getLibrary("compose.bom")
			implementation(platform(composeBom))
			implementation(libs.getBundle("compose"))
			debugImplementation(libs.getBundle("compose-debug"))
		}
	}
}
