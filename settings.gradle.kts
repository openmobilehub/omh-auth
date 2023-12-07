pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OMH Auth"
include(":auth-api")
include(":auth-sample")
include(":auth-api-mobileweb")
include(":auth-api-non-gms")
include(":auth-api-box")
include(":auth-api-dropbox")
include(":auth-api-mslive")
include(":auth-api-gms")
