import java.io.FileInputStream
import java.util.Properties

var properties = Properties()
properties.load(FileInputStream("local.properties"))

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.squirtles.firebase"
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
                "FIRESTORE_DB_ID",
                "\"${properties.getProperty("FIRESTORE_DB_ID_DEBUG")}\""
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
                "FIRESTORE_DB_ID",
                "\"${properties.getProperty("FIRESTORE_DB_ID_RELEASE")}\""
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
    implementation(projects.core.model)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.firebase.functions.ktx)
    ksp(libs.hilt.android.compiler)
    implementation(libs.inject)

    // Firebase
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.geofire.android.common)
}
