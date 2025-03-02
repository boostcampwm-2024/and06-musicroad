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
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(projects.data.firebase)
    implementation(projects.domain.favorite)
    implementation(projects.core.buildconfig)
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
