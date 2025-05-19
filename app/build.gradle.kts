import java.io.FileInputStream
import java.util.Properties

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(rootProject.file("app/keystore.properties")))

plugins {
    alias(libs.plugins.musicroad.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.squirtles.musicroad"

    defaultConfig {
        addManifestPlaceholders(mapOf("NAVERMAP_CLIENT_ID" to properties.getProperty("NAVERMAP_CLIENT_ID")))
    }

    signingConfigs {
        signingConfigs {
            create("signedDebug") {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }

            create("signedRelease") {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("signedDebug")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("signedRelease")
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.data.applemusic)
    implementation(projects.data.firebase)
    implementation(projects.data.user)
    implementation(projects.data.pick)
    implementation(projects.data.favorite)
    implementation(projects.data.location)
    implementation(projects.data.order)
    implementation(projects.data.preference)
    implementation(projects.feature.main)
    implementation(projects.feature.userinfo)
    implementation(projects.feature.search)
    implementation(projects.feature.create)
    implementation(projects.feature.favorite)
    implementation(projects.feature.mypick)
    implementation(projects.feature.detail)
    implementation(projects.feature.map)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Hilt
    implementation(libs.hilt.android)
    androidTestImplementation(libs.hilt.android.testing)
    ksp(libs.hilt.android.compiler)
    kspAndroidTest(libs.hilt.android.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.google.firebase.dynamic.module.support)
    implementation(libs.firebase.crashlytics)
}
