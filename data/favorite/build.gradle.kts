import java.io.FileInputStream
import java.util.Properties

var properties = Properties()
properties.load(FileInputStream("local.properties"))

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.squirtles.favorite"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false

            buildConfigField(
                "String",
                "HTTPS_CALLABLE",
                "\"${properties.getProperty("HTTPS_CALLABLE_DEBUG")}\""
            )
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "HTTPS_CALLABLE",
                "\"${properties.getProperty("HTTPS_CALLABLE_RELEASE")}\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.data.firebase)
    implementation(projects.domain.favorite)
    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // retrofit
    implementation(libs.retrofit.core)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.firebase.functions.ktx)
    ksp(libs.hilt.android.compiler)
    implementation(libs.inject)

    // Firebase
    implementation(libs.firebase.firestore.ktx)
}
