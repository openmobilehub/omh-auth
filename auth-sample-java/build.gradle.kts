import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    `android-application`
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.44" apply true
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.omh.android.auth.sample"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "google_services"
    productFlavors {
        create("ngms") {
            dimension = "google_services"
        }
        create("gms") {
            dimension = "google_services"
        }
        create("singleBuild") {
            dimension = "google_services"
        }
    }

    viewBinding {
        enable = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    kapt {
        correctErrorTypes = true
    }
}

val gmsImplementation by configurations
val ngmsImplementation by configurations
val singleBuildImplementation by configurations
dependencies {
    ngmsImplementation(project(":auth-api-non-gms"))
    gmsImplementation(project(":auth-api-gms"))

    singleBuildImplementation(project(":auth-api-non-gms"))
    singleBuildImplementation(project(":auth-api-gms"))

    implementation(Libs.googleApiClientAndroid)

    implementation(Libs.coreKtx)
    implementation(Libs.lifecycleKtx)
    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(Libs.googleSignIn)

    implementation("io.reactivex.rxjava3:rxjava:3.1.6")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    // Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")

    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}