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

    implementation("androidx.concurrent:concurrent-futures:1.1.0")
    // Kotlin
    implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")
    implementation("com.google.guava:guava:31.1-android")
    
    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}