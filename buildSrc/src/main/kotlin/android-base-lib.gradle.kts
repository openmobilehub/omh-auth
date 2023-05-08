import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
    kotlin("android")
    id("jacoco")
}

detekt {
    autoCorrect = properties.get("autoCorrect")?.toString()?.toBoolean() ?: false
}

android {
    compileSdk = ConfigData.compileSdkVersion
    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        vectorDrawables {
            useSupportLibrary = true
        }
        consumerProguardFiles("consumer-rules.pro")
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("**/LICENSE.txt")
        resources.excludes.add("**/README.txt")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

setupJacoco()

dependencies {
    detektPlugins(BuildPlugins.detekt)
}

ext {
    this["PUBLISH_GROUP_ID"] = getPropertyOrFail("group")
    this["PUBLISH_VERSION"] = getPropertyOrFail("version")
    this["PUBLISH_ARTIFACT_ID"] = properties.get("artifactId").toString()
}

apply {
    from("${rootProject.projectDir}/tools/scripts/publish-module.gradle")
}
