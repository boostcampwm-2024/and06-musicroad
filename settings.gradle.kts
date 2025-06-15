pluginManagement {

    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repository.map.naver.com/archive/maven")
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

rootProject.name = "MusicRoad"
include(":app")
include(":domain")
include(":data")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":core:model")
include(":core:navigation")
include(":core:util")
include(":core:common")
include(":core:picklist")
include(":core:mediaservice")
include(":domain:picklist")
include(":domain:applemusic")
include(":domain:favorite")
include(":domain:location")
include(":domain:order")
include(":domain:pick")
include(":domain:user")
include(":domain:player")
include(":data:applemusic")
include(":data:favorite")
include(":data:firebase")
include(":data:location")
include(":data:order")
include(":data:pick")
include(":data:user")
include(":core:musicplayer")
include(":core:account")
include(":feature:create")
include(":feature:favorite")
include(":core:buildconfig")
include(":feature:search")
include(":feature:userinfo")
include(":feature:mypick")
include(":feature:detail")
include(":feature:map")
include(":feature:main")
include(":domain:firebase")
include(":domain:preference")
include(":data:preference")
include(":core:preference")
include(":feature:permission")
