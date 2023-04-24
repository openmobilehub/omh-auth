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

    api("androidx.concurrent:concurrent-futures:1.1.0")
    // Kotlin
    api("androidx.concurrent:concurrent-futures-ktx:1.1.0")
    api("com.google.guava:guava:31.1-android")
    api("com.google.android.gms:play-services-tasks:18.0.2")
    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}