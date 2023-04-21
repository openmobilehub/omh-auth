plugins {
    `android-base-lib`
}

android {
    namespace = "com.omh.android.auth.gms"
}

dependencies {
    api(project(":auth-api"))

    // KTX
    implementation(Libs.coreKtx)

    // Coroutines
    implementation(Libs.coroutinesCore)
    implementation(Libs.coroutinesAndroid)
    implementation("androidx.concurrent:concurrent-futures:1.1.0")
    // Kotlin
    implementation("androidx.concurrent:concurrent-futures-ktx:1.1.0")
    implementation("com.google.guava:guava:31.1-android")
    // Google Sign In
    implementation(Libs.googleSignIn)
    implementation(Libs.googleApiClientAndroid)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    testImplementation(Libs.mockk)
    testImplementation(Libs.coroutineTesting)
}