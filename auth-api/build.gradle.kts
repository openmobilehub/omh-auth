plugins {
    `android-base-lib`
}

android {
    namespace = "com.omh.android.auth.api"
}

dependencies {

    implementation(Libs.androidAppCompat)
    implementation(Libs.reflection)

    api(Libs.googleSignIn)

    // Test dependencies
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.androidJunit)
    androidTestImplementation(Libs.esspreso)
}