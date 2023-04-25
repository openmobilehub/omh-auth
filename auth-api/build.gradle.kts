plugins {
    `android-base-lib`
}

android {
    namespace = "com.omh.android.auth.api"
}

dependencies {

    implementation(Libs.androidAppCompat)
    implementation(Libs.material)
    implementation(Libs.reflection)

    api("com.google.android.gms:play-services-tasks:18.0.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.1")
    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}