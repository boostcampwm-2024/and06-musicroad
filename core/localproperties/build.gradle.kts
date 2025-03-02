import java.io.FileInputStream
import java.util.Properties

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

plugins {
    alias(libs.plugins.musicroad.android.library)
}

android {
    namespace = "com.squirtles.localproperties"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {

        buildConfigField(
            "String",
            "GOOGLE_CLIENT_ID",
            "\"${properties.getProperty("GOOGLE_CLIENT_ID")}\""
        )

        buildConfigField(
            "String",
            "APPLE_MUSIC_API_TOKEN",
            "\"${properties.getProperty("APPLE_MUSIC_API_TOKEN")}\""
        )
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false

            buildConfigField(
                "String",
                "FIRESTORE_DB_ID",
                "\"${properties.getProperty("FIRESTORE_DB_ID_DEBUG")}\""
            )

            buildConfigField(
                "String",
                "HTTPS_CALLABLE",
                "\"${properties.getProperty("HTTPS_CALLABLE_DEBUG")}\""
            )
        }

        getByName("debug") {
            isMinifyEnabled = false

            buildConfigField(
                "String",
                "FIRESTORE_DB_ID",
                "\"${properties.getProperty("FIRESTORE_DB_ID_RELEASE")}\""
            )

            buildConfigField(
                "String",
                "HTTPS_CALLABLE",
                "\"${properties.getProperty("HTTPS_CALLABLE_RELEASE")}\""
            )
        }
    }
}

dependencies {

}
